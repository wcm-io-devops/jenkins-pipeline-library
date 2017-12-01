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
package vars.setScmUrl

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import org.junit.Test

import static org.junit.Assert.assertEquals

class SetScmUrlIT extends LibraryIntegrationTestBase {

  @Test
  void shouldUseScmUrlFromConfig() {
    loadAndExecuteScript("vars/setScmUrl/jobs/setScmUrlWithConfigJob.groovy")
    assertEquals("http://domain.tld/group/project.git", this.getEnv(EnvironmentConstants.SCM_URL))
  }

  @Test
  void shouldUseScmUrlFromShell() {
    loadAndExecuteScript("vars/setScmUrl/jobs/setScmUrlFromShellJob.groovy")
    assertEquals("http://remote.origin.url/group/project.git", this.getEnv(EnvironmentConstants.SCM_URL))
  }

}
