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

    NotificationTriggerHelper triggerHelper = notify.getTriggerHelper()
    String trigger = triggerHelper.getTrigger().toString()
    Object buildResultConfig =  notify.getBuildResultConfig(notifyConfig)
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
