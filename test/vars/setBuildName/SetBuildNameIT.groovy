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
package vars.setBuildName

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static org.junit.Assert.assertEquals

class SetBuildNameIT extends LibraryIntegrationTestBase {

  @Test
  void shouldUseGitBranch() {
    this.setEnv("BUILD_NUMBER", "1")
    this.setEnv("GIT_BRANCH", "I_AM_THE_GITBRANCH")
    loadAndExecuteScript("vars/setBuildName/jobs/setBuildNameJob.groovy")

    assertEquals("#1_I_AM_THE_GITBRANCH", this.context.getRunWrapperMock().getDisplayName())
  }

  @Test
  void shouldNotUseGitBranch() {
    this.setEnv("BUILD_NUMBER", "1")
    loadAndExecuteScript("vars/setBuildName/jobs/setBuildNameJob.groovy")

    assertEquals("#1", this.context.getRunWrapperMock().getDisplayName())
  }


}
