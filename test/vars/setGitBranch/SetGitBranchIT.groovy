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
package vars.setGitBranch

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import org.junit.Test

import static org.junit.Assert.assertEquals

class SetGitBranchIT extends LibraryIntegrationTestBase {

  @Test
  void shouldUseLocalBranchName() {
    helper.registerAllowedMethod(StepConstants.SH, [Map.class], shellMapCallback)
    loadAndExecuteScript("vars/setGitBranch/jobs/setGitBranchTestJob.groovy")

    assertEquals("my-custom-branch-name", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  @Test
  void shouldFallbackToHeadRev() {
    loadAndExecuteScript("vars/setGitBranch/jobs/setGitBranchTestJob.groovy")

    assertEquals("0HFGC0", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  @Test
  void shouldUseBranchNameEnvVar() {
    this.setEnv(EnvironmentConstants.BRANCH_NAME, "VALUE_OF_BRANCH_NAME")
    loadAndExecuteScript("vars/setGitBranch/jobs/setGitBranchTestJob.groovy")
    assertEquals("VALUE_OF_BRANCH_NAME", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  @Test
  void shouldUseGitBranchEnvVar() {
    this.setEnv(EnvironmentConstants.BRANCH_NAME, "VALUE_OF_GIT_BRANCH")
    loadAndExecuteScript("vars/setGitBranch/jobs/setGitBranchTestJob.groovy")
    assertEquals("VALUE_OF_GIT_BRANCH", this.getEnv(EnvironmentConstants.GIT_BRANCH))
  }

  def shellMapCallback = { Map incomingCommand ->
    stepRecorder.record(StepConstants.SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case "git branch": return "* my-custom-branch-name"
          break
        default: return ""
      }
    }
  }
}
