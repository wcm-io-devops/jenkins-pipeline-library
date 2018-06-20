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

package vars.gitTools.mirrorHttps

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.*
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.*
import static org.junit.Assert.assertEquals


class GitToolsMirrorHttpsIT extends LibraryIntegrationTestBase {

  List<Map> mockedShellCommands = []

  Boolean repoExists = false

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    this.credentialsPluginMock.mockUsernamePassword("host1-http-credential-id","host1user","host1password")
    this.credentialsPluginMock.mockUsernamePassword("host2-http-credential-id","host2user","host2password")
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
    helper.registerAllowedMethod(FILE_EXISTS, [String.class], fileExistsCallback)
  }

  @Test
  void shouldMirrorRepositoryWithCredentialAutoLookup() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorHttps/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 8)
    List expectedShellCalls = [
      "echo '#!/bin/bash\necho \"host1password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      "git clone --mirror https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git jenkins-pipeline-library.git",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
      "echo '#!/bin/bash\necho \"host2password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      "git remote set-url --push origin https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
    ]

    this.assertShellCalls(expectedShellCalls, actualShellCalls)

    assertNone(SSH_AGENT)

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
        result: "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorHttps/jobs/shouldMirrorRepositoryWithProvidedCredentialsTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 8)
    List expectedShellCalls = [
      "echo '#!/bin/bash\necho \"host1password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      "git clone --mirror https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git jenkins-pipeline-library.git",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
      "echo '#!/bin/bash\necho \"host2password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      "git remote set-url --push origin https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
    ]

    this.assertShellCalls(expectedShellCalls, actualShellCalls)

    assertNone(SSH_AGENT)

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
        result: "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    repoExists = true
    loadAndExecuteScript("vars/gitTools/mirrorHttps/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
    List actualShellCalls = assertStepCalls(SH, 9)
    List expectedShellCalls = [
      "echo '#!/bin/bash\necho \"host1password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git fetch -p origin",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
      "echo '#!/bin/bash\necho \"host2password\"' > '/path/to/workspace/.gitaskpass.sh' && chmod 700 '/path/to/workspace/.gitaskpass.sh'",
      "git remote set-url --push origin https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git",
      [
        script      : "git remote -v",
        returnStdout: true
      ],
      "git push --mirror",
      "rm -f '/path/to/workspace/.gitaskpass.sh'",
    ]
    this.assertShellCalls(expectedShellCalls, actualShellCalls)

    assertNone(SSH_AGENT)

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
        result: "origin  https://host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  https://host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    loadAndExecuteScript("vars/gitTools/mirrorHttps/jobs/shouldMirrorRepositoryWithCredentialAutoLookupTestJob.groovy")
  }

  void assertShellCalls(List<String> expectedShellCalls, List<String> actualShellcalls) {
    for (int i = 0; i < expectedShellCalls.size(); i++) {
      assertEquals("Shell call at index '$i' is wrong", expectedShellCalls.get(i), actualShellcalls.get(i))
    }
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
