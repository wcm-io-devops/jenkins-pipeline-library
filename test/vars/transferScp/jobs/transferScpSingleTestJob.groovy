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
package vars.transferScp.jobs

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs the transferScp step with ssh credential auto lookup (key + username)
 *
 * @return The script
 * @see vars.setScmUrl.SetScmUrlIT
 */
def execute() {
  transferScp(
      (SCP): [
          (SCP_HOST)          : "testserver1.testservers.domain.tld",
          (SCP_PORT)          : null,
          (SCP_USER)          : null,
          (SCP_ARGUMENTS)     : [],
          (SCP_RECURSIVE)     : false,
          (SCP_SOURCE)        : "/path/to/source",
          (SCP_DESTINATION)   : "/path/to/destination",
          (SCP_EXECUTABLE)    : null,
          (SCP_HOST_KEY_CHECK): false
      ]
  )
}

return this
