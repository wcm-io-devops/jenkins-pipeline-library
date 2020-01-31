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
package vars.gitTools.getBranch

import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static org.junit.Assert.assertEquals

class GitToolsGetBranchIT extends LibraryIntegrationTestBase {
  @Test
  void shouldUseLocalBranchName() {
    this.setEnv(EnvironmentConstants.GIT_LOCAL_BRANCH, "EXPECTED_GIT_LOCAL_BRANCH")
    this.setEnv(EnvironmentConstants.GIT_BRANCH, "EXPECTED_GIT_BRANCH")
    this.setEnv(EnvironmentConstants.BRANCH_NAME, "EXPECTED_BRANCH_NAME")
    loadAndExecuteScript("vars/gitTools/jobs/setGitBranchTestJob.groovy")
    assertEquals("EXPECTED_GIT_LOCAL_BRANCH", this.getEnv(EnvironmentConstants.GIT_LOCAL_BRANCH))
  }

  @Test
  void shouldFallbackToHeadRev() {
    this.setEnv(EnvironmentConstants.GIT_LOCAL_BRANCH, null)
    this.setEnv(EnvironmentConstants.GIT_BRANCH, null)
    this.setEnv(EnvironmentConstants.BRANCH_NAME, null)
    loadAndExecuteScript("vars/gitTools/jobs/setGitBranchTestJob.groovy")

    assertEquals("0HFGC0", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  @Test
  void shouldUseBranchNameEnvVar() {
    this.setEnv(EnvironmentConstants.GIT_LOCAL_BRANCH, null)
    this.setEnv(EnvironmentConstants.GIT_BRANCH, null)
    this.setEnv(EnvironmentConstants.BRANCH_NAME, "EXPECTED_BRANCH_NAME")
    loadAndExecuteScript("vars/gitTools/jobs/setGitBranchTestJob.groovy")
    assertEquals("EXPECTED_BRANCH_NAME", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  @Test
  void shouldUseGitBranchEnvVar() {
    this.setEnv(EnvironmentConstants.GIT_LOCAL_BRANCH, null)
    this.setEnv(EnvironmentConstants.GIT_BRANCH, "EXPECTED_GIT_BRANCH")
    this.setEnv(EnvironmentConstants.BRANCH_NAME, "EXPECTED_BRANCH_NAME")
    loadAndExecuteScript("vars/gitTools/jobs/setGitBranchTestJob.groovy")
    assertEquals("EXPECTED_GIT_BRANCH", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }
}
