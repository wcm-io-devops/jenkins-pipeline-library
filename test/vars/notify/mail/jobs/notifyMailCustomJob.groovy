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
package vars.notify.mail.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs notifyMail step with custom configuration (opposite of default configuration)
 *
 * @return The script
 * @see vars.notify.mail.NotifyMailCustomIT
 */
def execute() {
  notify.mail(
      [
          (NOTIFY): [
              (NOTIFY_ON_SUCCESS)         : true,
              (NOTIFY_ON_FAILURE)         : false,
              (NOTIFY_ON_STILL_FAILING)   : false,
              (NOTIFY_ON_FIXED)           : false,
              (NOTIFY_ON_UNSTABLE)        : false,
              (NOTIFY_ON_STILL_UNSTABLE)  : false,
              (NOTIFY_ON_ABORT)           : true,
              (NOTIFY_TO)                 : 'test@test.com',
              (NOTIFY_SUBJECT)            : 'custom mail subject with trigger: ${NOTIFICATION_TRIGGER}',
              (NOTIFY_BODY)               : 'custom body with trigger: ${NOTIFICATION_TRIGGER}',
              (NOTIFY_ATTACHMENTS_PATTERN): 'custom/pattern/**/*.txt',
              (NOTIFY_ATTACH_LOG)         : true,
              (NOTIFY_COMPRESS_LOG)       : true,
              (NOTIFY_MIME_TYPE)          : 'text/html'
          ]
      ]

  )
}

return this
