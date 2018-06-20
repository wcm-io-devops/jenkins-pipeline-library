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

package vars.gitTools.mirrorSsh

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase

import static io.wcm.testing.jenkins.pipeline.StepConstants.DIR
import static io.wcm.testing.jenkins.pipeline.StepConstants.SSH_AGENT
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.*
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.FILE_EXISTS
import static org.junit.Assert.*

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH


class GitToolsMirrorSshIT extends LibraryIntegrationTestBase {

  List<Map> mockedShellCommands = []

  Boolean repoExists = false

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
    helper.registerAllowedMethod(FILE_EXISTS, [String.class], fileExistsCallback)
  }

  @Test(expected = AbortException)
  void shouldFailOnInvalidSrcRepo() {
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldFailOnInvalidSrcRepoTestJob.groovy")
  }

  @Test(expected = AbortException)
  void shouldFailOnInvalidTargetRepo() {
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldFailOnInvalidTargetRepoTestJob.groovy")
  }

  @Test(expected = AbortException)
  void shouldFailOnIdenticalServersVariant1() {
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldFailOnIdenticalServersVariant1TestJob.groovy")
  }

  @Test(expected = AbortException)
  void shouldFailOnIdenticalServersVariant2() {
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldFailOnIdenticalServersVariant2TestJob.groovy")
  }

  @Test
  void shouldMirrorRepositoryWithCredentialAutoLookup() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 4)
    List expectedShellCalls = [
      "git clone --mirror git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git jenkins-pipeline-library.git",
      "git remote set-url --push origin git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror"
    ]
    assertEquals(expectedShellCalls, actualShellCalls)

    List expectedSshAgentCalls = [
      ["host1-ssh-credential-id"],
      ["host2-ssh-credential-id"]
    ]
    List actualSshAgentCalls = assertTwice(SSH_AGENT)
    assertEquals(expectedSshAgentCalls, actualSshAgentCalls)

    assertOnce(FILE_EXISTS)
    String expectedDirCall = "jenkins-pipeline-library.git"
    String actualDirCall = assertOnce(DIR)
    assertEquals(expectedDirCall, actualDirCall)
  }

  @Test
  void shouldMirrorRepositoryWithProvidedCredentials() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldMirrorRepositoryWithProvidedCredentialsTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 4)
    List expectedShellCalls = [
      "git clone --mirror git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git jenkins-pipeline-library.git",
      "git remote set-url --push origin git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror"
    ]
    assertEquals(expectedShellCalls, actualShellCalls)

    List expectedSshAgentCalls = [
      ["src-cred-1", "src-cred-2", "src-cred-3"],
      ["target-cred-1", "target-cred-2", "target-cred-3"]
    ]
    List actualSshAgentCalls = assertTwice(SSH_AGENT)
    assertEquals(expectedSshAgentCalls, actualSshAgentCalls)

    assertOnce(FILE_EXISTS)
    String expectedDirCall = "jenkins-pipeline-library.git"
    String actualDirCall = assertOnce(DIR)
    assertEquals(expectedDirCall, actualDirCall)
  }

  @Test
  void shouldFetchRepositoryWithCredentialAutoLookup() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    repoExists = true
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 5)
    List expectedShellCalls = [
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git fetch -p origin",
      "git remote set-url --push origin git@host2.domain.tld:wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror"
    ]
    assertEquals(expectedShellCalls, actualShellCalls)

    List expectedSshAgentCalls = [
      ["host1-ssh-credential-id"],
      ["host2-ssh-credential-id"]
    ]
    List actualSshAgentCalls = assertTwice(SSH_AGENT)
    assertEquals(expectedSshAgentCalls, actualSshAgentCalls)

    assertOnce(FILE_EXISTS)
    List expectedDirCalls = [
      "jenkins-pipeline-library.git",
      "jenkins-pipeline-library.git",
    ]

    List actualDirCalls = assertTwice(DIR)
    assertEquals(expectedDirCalls, actualDirCalls)
  }

  @Test(expected = AbortException)
  void shouldFailWhenRemotePushOriginIsWrongTestJob() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorSsh/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
  }

  def fileExistsCallback = {
    String file ->
      stepRecorder.record(FILE_EXISTS, file)
      return repoExists
  }

  def shellMapCallback = { Map incomingCommand ->
    stepRecorder.record(SH, incomingCommand)
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
