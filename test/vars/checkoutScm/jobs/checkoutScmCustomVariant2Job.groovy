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
package vars.checkoutScm.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Executes a custom checkout with the provided userRemoteConfigs that should not use the provided credentialsId and the scm url
 *
 * @return The script
 *
 * @see vars.checkoutScm.CheckoutScmIT
 */
def execute() {
  checkoutScm((SCM): [
      (SCM_CREDENTIALS_ID)     : 'NOT_USED_CREDENTIAL',
      (SCM_URL)                : 'NOT_USED_URL',
      (SCM_USER_REMOTE_CONFIGS): [[credentialsId: 'USER_REMOTE_CONFIGS_CREDENTIAL', url: 'USER_REMOTE_CONFIGS_URL']]
  ])
}

return this
