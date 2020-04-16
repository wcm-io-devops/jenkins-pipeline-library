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
package vars.notify.teams.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs notify.teams step with default configuration
 *
 * @return The script
 */
def execute() {
  Map config = [
    (NOTIFY_TEAMS) : [
      (NOTIFY_TEAMS_ENABLED)    : true,
      (NOTIFY_TEAMS_MESSAGE)    : "customMessage",
      (NOTIFY_TEAMS_WEBHOOK_URL): "https://customWebhookUrl",
      (NOTIFY_TEAMS_COLOR)      : "customColor",
      (NOTIFY_ON_SUCCESS)       : true,
      (NOTIFY_ON_FAILURE)       : false,
      (NOTIFY_ON_STILL_FAILING) : false,
      (NOTIFY_ON_FIXED)         : false,
      (NOTIFY_ON_UNSTABLE)      : false,
      (NOTIFY_ON_STILL_UNSTABLE): false,
      (NOTIFY_ON_ABORT)         : true,
    ]
  ]
  notify.teams(config)
}

return this
