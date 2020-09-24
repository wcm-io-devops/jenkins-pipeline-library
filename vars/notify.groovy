/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import io.wcm.devops.jenkins.pipeline.config.GenericConfigConstants
import io.wcm.devops.jenkins.pipeline.config.GenericConfigUtils
import io.wcm.devops.jenkins.pipeline.model.Result
import io.wcm.devops.jenkins.pipeline.utils.NotificationTriggerHelper
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.maps.MapMergeMode
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Used to send mail notifications at the end of a build.
 * This step brings back the "still failing", "still unstable" and "fixed"
 * functionality which is currently missing in the extmail step.
 *
 * @param config Configuration options for the step
 */
void mail(Map config = [:]) {
  Logger log = new Logger(this)

  Map defaultConfig = [
    (NOTIFY): [
      (MAP_MERGE_MODE)            : MapMergeMode.REPLACE,
      (NOTIFY_ATTACH_LOG)         : false,
      (NOTIFY_ATTACHMENTS_PATTERN): '',
      (NOTIFY_BODY)               : '${DEFAULT_CONTENT}',
      (NOTIFY_COMPRESS_LOG)       : false,
      (NOTIFY_ENABLED)            : true,
      (NOTIFY_MIME_TYPE)          : null,
      (NOTIFY_ON_SUCCESS)         : false,
      (NOTIFY_ON_UNSTABLE)        : true,
      (NOTIFY_ON_STILL_UNSTABLE)  : true,
      (NOTIFY_ON_FIXED)           : true,
      (NOTIFY_ON_FAILURE)         : true,
      (NOTIFY_ON_STILL_FAILING)   : true,
      (NOTIFY_ON_ABORT)           : false,
      (NOTIFY_RECIPIENT_PROVIDERS): [
        [$class: 'CulpritsRecipientProvider'],
        // Sends email to all the people who caused a change in the change set.
        [$class: 'DevelopersRecipientProvider'],
        // Sends email to the list of users suspected of causing the build to begin failing.
        [$class: 'FirstFailingBuildSuspectsRecipientProvider'],
        // Sends email to the user who initiated the build.
        [$class: 'RequesterRecipientProvider'],
        // Sends email to the list of users who committed changes in upstream builds that triggered this build.
        [$class: 'UpstreamComitterRecipientProvider']
      ],
      (NOTIFY_SUBJECT)            : '${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${NOTIFICATION_TRIGGER}',
      (NOTIFY_TO)                 : null,
    ]
  ]

  GenericConfigUtils genericConfigUtils = new GenericConfigUtils(this)
  String search = genericConfigUtils.getFQJN()
  log.info("Fully-Qualified Job Name (FQJN)", search)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.NOTIFY_MAIL_CONFIG_PATH, search, NOTIFY)

  // merge default config with config from yaml and incoming yaml
  config = MapUtils.merge(defaultConfig, yamlConfig, config)

  // retrieve the configuration and set defaults
  Map notifyConfig = (Map) config[NOTIFY] ?: [:]

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  String trigger = triggerHelper.getTrigger().toString()
  Object buildResultConfig = this.getBuildResultConfig(notifyConfig)
  if (buildResultConfig == false) {
    // notification is disabled in the build result specific configuration
    return
  }
  notifyConfig = buildResultConfig

  // return when notify is not enabled
  Boolean enabled = notifyConfig[NOTIFY_ENABLED]
  if (!enabled) {
    return
  }

  // parse recipient providers
  recipientProviders = notifyConfig[NOTIFY_RECIPIENT_PROVIDERS]

  // parse values
  String subject = notifyConfig[NOTIFY_SUBJECT]
  String body = notifyConfig[NOTIFY_BODY]
  String to = notifyConfig[NOTIFY_TO]

  String attachmentsPattern = notifyConfig[NOTIFY_ATTACHMENTS_PATTERN]
  Boolean attachLog = notifyConfig[NOTIFY_ATTACH_LOG]
  Boolean compressLog = notifyConfig[NOTIFY_COMPRESS_LOG]
  String mimeType = notifyConfig[NOTIFY_MIME_TYPE]

  // replace notification trigger variable because extmail step does not know about it
  subject = triggerHelper.replaceEnvVar(subject, trigger)
  body = triggerHelper.replaceEnvVar(body, trigger)

  log.trace("value of envVar ${env.NOTIFICATION_TRIGGER}")

  log.info("Sending notification for: " + trigger)

  // send the notification
  emailext(
    subject: subject,
    body: body,
    attachLog: attachLog,
    attachmentsPattern: attachmentsPattern,
    compressLog: compressLog,
    mimeType: mimeType,
    recipientProviders: recipientProviders,
    to: to
  )
}

/**
 * Sends an MQTT notification using the MQTT Notification Plugin
 *
 * @param config The configuration for the step
 */
void mqtt(Map config = [:]) {

  Logger log = new Logger("notify.mqtt")

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  Result buildResult = triggerHelper.getTrigger()

  Integer timestamp = new Date().getTime() / 1000l

  String defaultMqttMessage = """\
    BUILD_NUMBER: ${Integer.parseInt(env.getProperty("BUILD_NUMBER"))}
    BUILD_RESULT: '${buildResult.toString()}'
    BUILD_RESULT_COLOR: '${buildResult.getColor()}'
    BUILD_URL: '${env.getProperty("BUILD_URL")}'
    JENKINS_URL: '${env.getProperty("JENKINS_URL")}'
    JOB_BASE_NAME: '${env.getProperty("JOB_BASE_NAME")}'
    JOB_DISPLAY_URL: '${env.getProperty("JOB_DISPLAY_URL")}'
    JOB_NAME: '${env.getProperty("JOB_NAME")}'
    RUN_CHANGES_DISPLAY_URL: '${env.getProperty("RUN_CHANGES_DISPLAY_URL")}'
    TIMESTAMP: ${timestamp}"""

  Map defaultConfig = [
    (NOTIFY_MQTT): [
      (MAP_MERGE_MODE)            : (MapMergeMode.REPLACE),
      (NOTIFY_MQTT_BROKER)        : null,
      (NOTIFY_MQTT_ENABLED)       : true,
      (NOTIFY_MQTT_CREDENTIALS_ID): '',
      (NOTIFY_MQTT_MESSAGE)       : defaultMqttMessage,
      (NOTIFY_MQTT_QOS)           : "1",
      (NOTIFY_MQTT_RETAIN)        : false,
      (NOTIFY_MQTT_TOPIC)         : "jenkins/${env.getProperty('JOB_NAME')}",
    ]
  ]
  GenericConfigUtils genericConfigUtils = new GenericConfigUtils(this)
  String search = genericConfigUtils.getFQJN()
  log.info("Fully-Qualified Job Name (FQJN)", search)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.NOTIFY_MQTT_CONFIG_PATH, search, NOTIFY_MQTT)

  // merge default config with config from yaml and incoming yaml
  config = MapUtils.merge(defaultConfig, yamlConfig, config)

  Map mqttConfig = config[NOTIFY_MQTT]
  Boolean mqttEnabled = mqttConfig[NOTIFY_MQTT_ENABLED]

  if (!mqttEnabled) {
    log.info("mqtt notification is disabled.")
    return
  }

  String broker = mqttConfig[NOTIFY_MQTT_BROKER]

  if (broker == null || broker.isEmpty()) {
    String msg = "broker (NOTIFY_MQTT_BROKER) needs to be defined for sending mqtt messages."
    log.warn(msg)
    return
  }

  log.debug("mqttConfig", mqttConfig)

  String credentialId = mqttConfig[NOTIFY_MQTT_CREDENTIALS_ID]
  String topic = mqttConfig[NOTIFY_MQTT_TOPIC]
  String message = mqttConfig[NOTIFY_MQTT_MESSAGE]
  qos = mqttConfig[NOTIFY_MQTT_QOS]
  Boolean retainMessage = mqttConfig[NOTIFY_MQTT_RETAIN]

  log.info("publish MQTT message for topic", topic)

  try {
    mqttNotification(brokerUrl: broker, credentialsId: credentialId, message: message, qos: qos, retainMessage: retainMessage, topic: topic)
  } catch (Exception ex) {
    log.error("Unable to send mqtt notification. Ensure that this step has a workspace to run in.", ex.getCause().toString())
  }
}

/**
 * Sends a mattermost notification using the Mattermost Notification Plugin
 *
 * @param config The configuration for the step
 */
void mattermost(Map config = [:]) {

  Logger log = new Logger("notify.mattermost")

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  String jobBaseName = env.getProperty('JOB_BASE_NAME').replace("%2F", "/")

  String defaultMattermostMessage = "**${triggerHelper.getTrigger()}** - <${env.BUILD_URL}console|${jobBaseName}#${env.BUILD_NUMBER}>"
  String defaultColor = triggerHelper.getTrigger().getColor()

  Map defaultConfig = [
    (NOTIFY_MATTERMOST): [
      (MAP_MERGE_MODE)                          : (MapMergeMode.REPLACE),
      (NOTIFY_MATTERMOST_ENABLED)               : true,
      (NOTIFY_MATTERMOST_CHANNEL)               : null,
      (NOTIFY_MATTERMOST_ENDPOINT)              : null,
      (NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID): null,
      (NOTIFY_MATTERMOST_ICON)                  : null,
      (NOTIFY_MATTERMOST_COLOR)                 : defaultColor,
      (NOTIFY_MATTERMOST_TEXT)                  : null,
      (NOTIFY_MATTERMOST_MESSAGE)               : defaultMattermostMessage,
      (NOTIFY_MATTERMOST_FAIL_ON_ERROR)         : false,
      (NOTIFY_ON_ABORT)                         : false,
      (NOTIFY_ON_FAILURE)                       : true,
      (NOTIFY_ON_STILL_FAILING)                 : true,
      (NOTIFY_ON_FIXED)                         : true,
      (NOTIFY_ON_SUCCESS)                       : false,
      (NOTIFY_ON_UNSTABLE)                      : true,
      (NOTIFY_ON_STILL_UNSTABLE)                : true,
    ]
  ]

  GenericConfigUtils genericConfigUtils = new GenericConfigUtils(this)
  String search = genericConfigUtils.getFQJN()
  log.info("Fully-Qualified Job Name (FQJN)", search)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.NOTIFY_MATTERMOST_CONFIG_PATH, search, NOTIFY_MATTERMOST)

  // merge default config with config from yaml and incoming yaml
  config = MapUtils.merge(defaultConfig, yamlConfig, config)

  // ease access to mattermost config values
  Map mattermostConfig = config[NOTIFY_MATTERMOST]

  if (!mattermostConfig[NOTIFY_MATTERMOST_ENABLED]) {
    log.info("mattermost notifications are disabled")
    return
  }

  // get build result specific configuration
  Object buildResultConfig = this.getBuildResultConfig(mattermostConfig)
  if (buildResultConfig == false) {
    // notification is disabled in the build result specific configuration
    return
  }

  log.debug("buildResultConfig", buildResultConfig)

  im.mattermost((NOTIFY_MATTERMOST): buildResultConfig)
}

/**
 * Sends a Microsoft Teams notification using the Office365 Connector plugin
 *
 * @param config The configuration for the step
 */
void teams(Map config = [:]) {

  Logger log = new Logger("notify.teams")

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  String defaultColor = triggerHelper.getTrigger().getColor()

  Map defaultConfig = [
    (NOTIFY_TEAMS): [
      (MAP_MERGE_MODE)                        : (MapMergeMode.REPLACE),
      (NOTIFY_TEAMS_ENABLED)                  : true,
      (NOTIFY_TEAMS_MESSAGE)                  : null,
      (NOTIFY_TEAMS_WEBHOOK_URL)              : null,
      (NOTIFY_TEAMS_WEBHOOK_URL_CREDENTIAL_ID): null,
      (NOTIFY_TEAMS_COLOR)                    : defaultColor,
      (NOTIFY_ON_ABORT)                       : false,
      (NOTIFY_ON_FAILURE)                     : true,
      (NOTIFY_ON_STILL_FAILING)               : true,
      (NOTIFY_ON_FIXED)                       : true,
      (NOTIFY_ON_SUCCESS)                     : false,
      (NOTIFY_ON_UNSTABLE)                    : true,
      (NOTIFY_ON_STILL_UNSTABLE)              : true,
    ]
  ]

  GenericConfigUtils genericConfigUtils = new GenericConfigUtils(this)
  String search = genericConfigUtils.getFQJN()
  log.info("Fully-Qualified Job Name (FQJN)", search)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.NOTIFY_TEAMS_CONFIG_PATH, search, NOTIFY_TEAMS)

  // merge default config with config from yaml and incoming yaml
  config = MapUtils.merge(defaultConfig, yamlConfig, config)

  // ease access to MS Teams config values
  Map teamsConfig = config[NOTIFY_TEAMS]

  if (!teamsConfig[NOTIFY_TEAMS_ENABLED]) {
    log.info("MS Teams notifications are disabled")
    return
  }

  // get build result specific configuration
  Object buildResultConfig = this.getBuildResultConfig(teamsConfig)
  if (buildResultConfig == false) {
    // notification is disabled in the build result specific configuration
    return
  }

  log.debug("buildResultConfig", buildResultConfig)

  im.teams((NOTIFY_TEAMS): buildResultConfig)

}

/**
 * Returns the notification config based on the build result
 *
 * @param config
 * @return the config or false when notification is not enabled
 */
Object getBuildResultConfig(Map config) {
  Logger log = new Logger('notify.getBuildResultConfig')


// parse status configurations
  def onSuccess = config[NOTIFY_ON_SUCCESS] != null ? config[NOTIFY_ON_SUCCESS] : false
  def onUnstable = config[NOTIFY_ON_UNSTABLE] != null ? config[NOTIFY_ON_UNSTABLE] : true
  def onStillUnstable = config[NOTIFY_ON_STILL_UNSTABLE] != null ? config[NOTIFY_ON_STILL_UNSTABLE] : true
  def onFixed = config[NOTIFY_ON_FIXED] != null ? config[NOTIFY_ON_FIXED] : true
  def onFailure = config[NOTIFY_ON_FAILURE] != null ? config[NOTIFY_ON_FAILURE] : true
  def onStillFailing = config[NOTIFY_ON_STILL_FAILING] != null ? config[NOTIFY_ON_STILL_FAILING] : true
  def onAbort = config[NOTIFY_ON_ABORT] != null ? config[NOTIFY_ON_ABORT] : false

  // calculate the notification trigger
  NotificationTriggerHelper triggerHelper = getTriggerHelper()
  String trigger = triggerHelper.getTrigger().toString()

  // set the environment variable
  env.setProperty(NotificationTriggerHelper.ENV_TRIGGER, trigger)

  def calculatedStatusConfig = [:]

  // check if notification is configured for trigger and apply custom configurations if configured
  switch (true) {
    case triggerHelper.isSuccess() && (onSuccess != false):
      calculatedStatusConfig = onSuccess
      break
    case triggerHelper.isFixed() && (onFixed != false):
      calculatedStatusConfig = onFixed
      break
    case triggerHelper.isUnstable() && (onUnstable != false):
      calculatedStatusConfig = onUnstable
      break
    case triggerHelper.isStillUnstable() && (onStillUnstable != false):
      calculatedStatusConfig = onStillUnstable
      break
    case triggerHelper.isFailure() && (onFailure != false):
      calculatedStatusConfig = onFailure
      break
    case triggerHelper.isStillFailing() && (onStillFailing != false):
      calculatedStatusConfig = onStillFailing
      break
    case triggerHelper.isAborted() && (onAbort != false):
      calculatedStatusConfig = onAbort
      break
    default:
      // return by default when previous block was not evaluated as true
      log.info("Notification not enabled for: " + trigger)
      return false
      break
  }
  // merge notify config with status specific configuration (if applicable)
  return mergeStatusConfig(config, calculatedStatusConfig)
}

/**
 * Merges the status specific configuration with the default configuration when applicable
 *
 * @param notifyConfig The notify config
 * @param statusCfg The status config
 * @return The merge configuration
 */
Map mergeStatusConfig(Map notifyConfig, def statusCfg) {
  Map ret = notifyConfig
  TypeUtils typeUtils = new TypeUtils()
  if (typeUtils.isMap(statusCfg)) {
    ret = MapUtils.merge(ret, statusCfg)
  }
  return ret
}

NotificationTriggerHelper getTriggerHelper() {
  Logger log = new Logger("notify.getTriggerHelper")
  // retrieve the current and previous build result
  String currentBuildResult = currentBuild.result
  String previousBuildResult = null
  previousBuild = currentBuild.getPreviousBuild()
  if (previousBuild) {
    previousBuildResult = previousBuild.result
  }

  log.trace("currentBuildResult", currentBuildResult)
  log.trace("previousBuildResult", previousBuildResult)

  // calculate the notification trigger
  return new NotificationTriggerHelper(currentBuildResult, previousBuildResult)
}

/**
 * Utility function to get the default recipient providers
 *
 * @param notifyConfig The notify config
 * @return The recipient providers
 */
List _getRecipientProviders(Map notifyConfig) {
  // configure the recipient providers
  // see https://jenkins.io/doc/pipeline/steps/email-ext/
  List ret = notifyConfig[NOTIFY_RECIPIENT_PROVIDERS] != null ? notifyConfig[NOTIFY_RECIPIENT_PROVIDERS] : [
    // list of users who committed change since last non broken build till now
    [$class: 'CulpritsRecipientProvider'],

    // Sends email to all the people who caused a change in the change set.
    [$class: 'DevelopersRecipientProvider'],

    // Sends email to the list of users suspected of causing the build to begin failing.
    [$class: 'FirstFailingBuildSuspectsRecipientProvider'],

    // Sends email to the user who initiated the build.
    [$class: 'RequesterRecipientProvider'],

    // Sends email to the list of users who committed changes in upstream builds that triggered this build.
    [$class: 'UpstreamComitterRecipientProvider']
  ]
  return ret
}
