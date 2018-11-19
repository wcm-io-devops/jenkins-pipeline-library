/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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
package vars.gitTools

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH

class GitToolsIT extends LibraryIntegrationTestBase {

  List<Map> mockedShellCommands = []

  Boolean repoExists = false

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
  }

  @Test
  void shouldReturnFetchOriginWithParam() {
    String remotes = "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
      "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
    String expectedFetchOrigin = "https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git"
    String actualFetchOrigin = loadAndExecuteScript("vars/gitTools/jobs/getFetchOriginTestJob.groovy", [ remotes: remotes])
    Assert.assertEquals(expectedFetchOrigin, actualFetchOrigin)
  }

  @Test
  void shouldReturnFetchOriginWithGetRemotes() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    String expectedFetchOrigin = "git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git"
    String actualFetchOrigin = loadAndExecuteScript("vars/gitTools/jobs/getFetchOriginTestJob.groovy")
    Assert.assertEquals(expectedFetchOrigin, actualFetchOrigin)
  }

  @Test
  void shouldReturnPushOriginWithParam() {
    String remotes = "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
      "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
    String expectedPushOrigin = "https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git"
    String actualPushOrigin = loadAndExecuteScript("vars/gitTools/jobs/getPushOriginTestJob.groovy", [ remotes: remotes])
    Assert.assertEquals(expectedPushOrigin, actualPushOrigin)
  }

  @Test
  void shouldReturnPushOriginWithGetRemotes() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    String expectedPushOrigin = "git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git"
    String actualPushOrigin = loadAndExecuteScript("vars/gitTools/jobs/getPushOriginTestJob.groovy")
    Assert.assertEquals(expectedPushOrigin, actualPushOrigin)
  }

  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    Boolean returnStatus = incomingCommand.returnStatus ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      for (Map mockedShellCommand in mockedShellCommands) {
        String mockedScript = mockedShellCommand.getOrDefault("script", "")
        String mockedResult = mockedShellCommand.getOrDefault("result", "")
        if (mockedScript == script) {
          return mockedResult
        }
      }
    }
    if (returnStatus) {
      switch (script) {
        default:
          return -1
      }
    }
    return null
  }
}
