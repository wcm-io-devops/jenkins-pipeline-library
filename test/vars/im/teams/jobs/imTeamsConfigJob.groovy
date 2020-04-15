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
package vars.im.teams.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_TEAMS
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_TEAMS_COLOR
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_TEAMS_MESSAGE
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_TEAMS_WEBHOOK_URL

/**
 * Runs im.teams step with default configuration
 *
 * @return The script
 */
def execute() {
  Map config = [
    (NOTIFY_TEAMS): [
      (NOTIFY_TEAMS_MESSAGE)                  : "configMessage",
      (NOTIFY_TEAMS_WEBHOOK_URL)              : "configWebhookUrl",
      (NOTIFY_TEAMS_COLOR)                    : "configColor",
    ]
  ]

  im.teams(config)
}


return this
