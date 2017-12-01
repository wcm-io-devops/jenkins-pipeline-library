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
package vars.execNpm.jobs

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.SCM
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.SCM_URL

/**
 * Runs execNpm step  with default configuration by providing only the scm url
 *
 * @return The script
 * @see vars.execMaven.ExecMavenIT
 */
def execute() {
  execNpm((SCM): [(SCM_URL): "https://subdomain.domain-new.tld/group/project1.git"])
}

return this
