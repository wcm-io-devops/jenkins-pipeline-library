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
package vars.im.mattermost.jobs

import io.wcm.devops.jenkins.pipeline.utils.maps.MapMergeMode

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.MAP_MERGE_MODE
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_CHANNEL
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_COLOR
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_FAIL_ON_ERROR
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_ICON
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_MESSAGE
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.NOTIFY_MATTERMOST_TEXT

/**
 * Runs notify.mattermost step with default configuration
 *
 * @return The script
 */
def execute() {
  Map config = [
    (NOTIFY_MATTERMOST): [
      (NOTIFY_MATTERMOST_CHANNEL)               : "configChannel",
      (NOTIFY_MATTERMOST_ENDPOINT_CREDENTIAL_ID): "configEndpointCredentialId",
      (NOTIFY_MATTERMOST_ICON)                  : "configIcon",
      (NOTIFY_MATTERMOST_COLOR)                 : "configColor",
      (NOTIFY_MATTERMOST_TEXT)                  : "configText",
      (NOTIFY_MATTERMOST_MESSAGE)               : "configMessage",
      (NOTIFY_MATTERMOST_FAIL_ON_ERROR)         : "configFailOnError",
    ]
  ]

  im.mattermost(config)
}


return this
