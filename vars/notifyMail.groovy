/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io DevOps
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

import io.wcm.devops.jenkins.pipeline.utils.NotificationTriggerHelper
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

import java.lang.reflect.Type

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Used to send notifications at the end of a build.
 * This step brings back the "still failing", "still unstable" and "fixed"
 * functionality which is currently missing in the extmail step.
 *
 * @param config Configuration options for the step
 * @see <a href="https://jenkins.io/doc/pipeline/steps/email-ext/">email-ext step</href>
 */
void call(Map config = [:]) {
    Logger log = new Logger(this)
    TypeUtils typeUtils = new TypeUtils()
    // retrieve the configuration and set defaults
    Map notifyConfig = (Map) config[NOTIFY] ?: [:]

    // early return when notify is not enabled
    Boolean enabled = notifyConfig[NOTIFY_ENABLED] != null ? notifyConfig[NOTIFY_ENABLED] : true
    if (!enabled) {
        return
    }

    // parse status configurations
    def onSuccess = notifyConfig[NOTIFY_ON_SUCCESS] != null ? notifyConfig[NOTIFY_ON_SUCCESS] : false
    def onUnstable = notifyConfig[NOTIFY_ON_UNSTABLE] != null ? notifyConfig[NOTIFY_ON_UNSTABLE] : true
    def onStillUnstable = notifyConfig[NOTIFY_ON_STILL_UNSTABLE] != null ? notifyConfig[NOTIFY_ON_STILL_UNSTABLE] : true
    def onFixed = notifyConfig[NOTIFY_ON_FIXED] != null ? notifyConfig[NOTIFY_ON_FIXED] : true
    def onFailure = notifyConfig[NOTIFY_ON_FAILURE] != null ? notifyConfig[NOTIFY_ON_FAILURE] : true
    def onStillFailing = notifyConfig[NOTIFY_ON_STILL_FAILING] != null ? notifyConfig[NOTIFY_ON_STILL_FAILING] : true
    def onAbort = notifyConfig[NOTIFY_ON_ABORT] != null ? notifyConfig[NOTIFY_ON_ABORT] : false

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
    NotificationTriggerHelper triggerHelper = new NotificationTriggerHelper(currentBuildResult, previousBuildResult)
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
            return
            break
    }
    // merge notify config with status specific configuration (if applicable)
    notifyConfig = _mergeStatusConfig(notifyConfig,calculatedStatusConfig)

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

Map _mergeStatusConfig(Map notifyConfig, def statusCfg) {
  Map ret = notifyConfig
  TypeUtils typeUtils = new TypeUtils()
  if (typeUtils.isMap(statusCfg)) {
    ret = MapUtils.merge(ret, statusCfg)
  }
  return ret
}

List _getRecipientProviders(Map notifyConfig) {
  // configure the recipient providers
  // see https://jenkins.io/doc/pipeline/steps/email-ext/
  List ret = notifyConfig[NOTIFY_RECIPIENT_PROVIDERS] != null ? notifyConfig[NOTIFY_RECIPIENT_PROVIDERS] : [
    // list of users who committed change since last non broken build till now
    [$class: 'CulpritsRecipientProvider'],

    // Sends email to all the people who caused a change in the change set.
    [$class: 'DevelopersRecipientProvider'],

    // Sends email to the list of users suspected of causing a unit test to begin failing
    //[$class: 'FailingTestSuspectsRecipientProvider'],

    // Sends email to the list of users suspected of causing the build to begin failing.
    [$class: 'FirstFailingBuildSuspectsRecipientProvider'],

    // Sends email to the list of recipients defined in the "Project Recipient List."
    // seems to work only when project based ACLs are present
    //[$class: 'ListRecipientProvider'],

    // Sends email to the user who initiated the build.
    [$class: 'RequesterRecipientProvider'],

    // Sends email to the list of users who committed changes in upstream builds that triggered this build.
    [$class: 'UpstreamComitterRecipientProvider']
  ]
  return ret
}
