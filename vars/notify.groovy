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
  TypeUtils typeUtils = new TypeUtils()
  // retrieve the configuration and set defaults
  Map notifyConfig = (Map) config[NOTIFY] ?: [:]

  // early return when notify is not enabled
  Boolean enabled = notifyConfig[NOTIFY_ENABLED] != null ? notifyConfig[NOTIFY_ENABLED] : true
  if (!enabled) {
    return
  }

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  String trigger = triggerHelper.getTrigger().toString()
  Object buildResultConfig = this.getBuildResultConfig(notifyConfig)
  if (buildResultConfig == false) {
    // notification is disabled in the build result specific configuration
    return
  }
  notifyConfig = buildResultConfig

  // parse recipient providers
  recipientProviders = _getRecipientProviders(notifyConfig)

  // parse values
  String subject = notifyConfig[NOTIFY_SUBJECT] ?: '${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${NOTIFICATION_TRIGGER}'
  String body = notifyConfig[NOTIFY_BODY] ?: '${DEFAULT_CONTENT}'
  String to = notifyConfig[NOTIFY_TO]

  String attachmentsPattern = notifyConfig[NOTIFY_ATTACHMENTS_PATTERN] ?: ''
  Boolean attachLog = notifyConfig[NOTIFY_ATTACH_LOG] != null ? notifyConfig[NOTIFY_ATTACH_LOG] : false
  Boolean compressLog = notifyConfig[NOTIFY_COMPRESS_LOG] != null ? notifyConfig[NOTIFY_COMPRESS_LOG] : false
  String mimeType = notifyConfig[NOTIFY_MIME_TYPE] != null ? notifyConfig[NOTIFY_MIME_TYPE] : null

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
 * Sends a MQTT notification using the MQTT Notification Plugin
 *
 * @param config The configuration for the step
 */
void mqtt(Map config = [:]) {

  Logger log = new Logger("notify.mqtt")

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  Result buildResult = triggerHelper.getTrigger()

  Integer timestamp = Integer.parseInt(sh(script: "echo \$(date +%s)", returnStdout: true).trim())

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

  String scmUrl = getScmUrl(config)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.MQTT_CONFIG_PATH, scmUrl, NOTIFY_MQTT)

  // merge default config with config from yaml and incoming yaml
  config = MapUtils.merge(defaultConfig, yamlConfig, config)

  Map mqttConfig = config[NOTIFY_MQTT]
  Boolean mqttEnabled = mqttConfig[NOTIFY_MQTT_ENABLED]

  if (!mqttEnabled) {
    log.info("mqtt notification is disabled.")
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

  mqttNotification(brokerUrl: broker, credentialsId: credentialId, message: message, qos: qos, retainMessage: retainMessage, topic: topic)
}

/**
 * Sends a mattermost notification using the Mattermost Notification Plugin
 *
 * @param config The configuration for the step
 */
void mattermost(Map config = [:]) {

  Logger log = new Logger("notify.mattermost")

  NotificationTriggerHelper triggerHelper = this.getTriggerHelper()
  String jobBaseName = env.getProperty('JOB_BASE_NAME').replace("%2F","/")

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

  String scmUrl = getScmUrl(config)

  // load yamlConfig
  Map yamlConfig = genericConfig.load(GenericConfigConstants.MATTERMOST_CONFIG_PATH, scmUrl, NOTIFY_MATTERMOST)

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

  mattermostConfig = buildResultConfig

  // use specific endpoint if configured
  if (mattermostConfig[NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID] != null && mattermostConfig[NOTIFY_MATTERMOST_ENDPOINT] == null) {
    log.debug("configure endpoint usind provided credential id ")
    withCredentials([
      string(credentialsId: mattermostConfig[NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID], variable: 'MATTERMOST_ENDPOINT')
    ]) {
      mattermostConfig[NOTIFY_MATTERMOST_ENDPOINT] = "${MATTERMOST_ENDPOINT}"
    }
  }

  // cleanup config and only pass allowed names parameters
  Map cleanedParams = [:]

  String[] allowedParams = [
    "channel",
    "endpoint",
    "icon",
    "color",
    "text",
    "message",
    "failOnError",
  ]

  for (String allowedParam in allowedParams) {
    if (mattermostConfig[allowedParam]) {
      cleanedParams[allowedParam] = mattermostConfig[allowedParam]
    }
  }

  log.debug("mattermostConfig", mattermostConfig)
  log.debug("cleanedParams", cleanedParams)

  // finally notify
  mattermostSend(cleanedParams)
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
