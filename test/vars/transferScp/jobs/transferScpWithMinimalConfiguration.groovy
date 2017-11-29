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
 * Runs the setScmUrl step in auto detection mode where scm url is determined via command line
 *
 * @return The script
 * @see vars.setScmUrl.SetScmUrlIT
 */
def execute() {
  transferScp(
      (SCP): [
          (SCP_HOST)       : "minimal.domain.tld",
          (SCP_SOURCE)     : '"/path/to/minimal source"',
          (SCP_DESTINATION): '"/path/to/minimal destination"'
      ]
  )
}

return this
