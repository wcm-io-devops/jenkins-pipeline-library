/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io DevOps
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
package vars.managedScripts

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILEPROVIDER
import static io.wcm.testing.jenkins.pipeline.StepConstants.SH
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertTwice
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MangedJenkinsShellScriptIT extends LibraryIntegrationTestBase {

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
  }

  @Test
  void shouldExecuteJenkinsShellScriptWithDefaultsTestJob() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/jenkinsShellScript/shouldExecuteJenkinsShellScriptWithDefaultsTestJob.groovy")
    List configFileCommand = assertOnce(CONFIGFILEPROVIDER)
    List shellCommands = assertTwice(SH)

    commonAssertions(configFileCommand, shellCommands, 'jenkins-script-id-1')


    Map expectedManagedScriptShellCommand = [
      'script': './.jenkinsShellScript_jenkins-script-id-1 jenkinsScript/path/1'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertNull(scriptResult)
  }

  @Test
  void shouldExecuteJenkinsShellScriptWithReturnStatusTestJob() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/jenkinsShellScript/shouldExecuteJenkinsShellScriptWithReturnStatusTestJob.groovy")
    List configFileCommand = assertOnce(CONFIGFILEPROVIDER)
    List shellCommands = assertTwice(SH)

    commonAssertions(configFileCommand, shellCommands,'jenkins-script-id-2')

    Map expectedManagedScriptShellCommand = [
      'returnStatus' : true,
      'script': './.jenkinsShellScript_jenkins-script-id-2 jenkinsScript/path/2 arg1 arg2'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals(3,scriptResult)
  }

  @Test
  void shouldExecuteJenkinsShellScriptWithReturnStdoutStatusTestJob() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/jenkinsShellScript/shouldExecuteJenkinsShellScriptWithReturnStdoutStatusTestJob.groovy")
    List configFileCommand = assertOnce(CONFIGFILEPROVIDER)
    List shellCommands = assertTwice(SH)

    commonAssertions(configFileCommand, shellCommands, 'jenkins-script-id-3')

    Map expectedManagedScriptShellCommand = [
      'returnStdout' : true,
      'script': './.jenkinsShellScript_jenkins-script-id-3 jenkinsScript/path/3 argName argValue'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals("hello world",scriptResult)
  }

  @Test
  void shouldExecuteJenkinsShellScriptWithReturnStdoutTestJob() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/jenkinsShellScript/shouldExecuteJenkinsShellScriptWithReturnStdoutTestJob.groovy")
    List configFileCommand = assertOnce(CONFIGFILEPROVIDER)
    List shellCommands = assertTwice(SH)

    commonAssertions(configFileCommand, shellCommands, 'jenkins-script-id-4')

    Map expectedManagedScriptShellCommand = [
      'returnStdout' : true,
      'script': './.jenkinsShellScript_jenkins-script-id-4 jenkinsScript/path/4 arg3 arg4'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals('hello world',scriptResult)
  }

  def shellMapCallback = { Map incomingCommand ->
    stepRecorder.record(SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    Boolean returnStatus = incomingCommand.returnStatus ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case './.jenkinsShellScript_jenkins-script-id-4 jenkinsScript/path/4 arg3 arg4':
          return "hello world\n"
          break
        case './.jenkinsShellScript_jenkins-script-id-3 jenkinsScript/path/3 argName argValue':
          return "hello world\n\t     \n"
          break
        default:
          return ""
      }
    }
    if (returnStatus) {
      switch (script) {
        case './.jenkinsShellScript_jenkins-script-id-2 jenkinsScript/path/2 arg1 arg2':
          return 3
          break
        default:
          return -1
      }
    }
    return null
  }

  void commonAssertions(List configFileCommand, List shellCommands, String expectedJenkinsScriptId) {
    assertEquals(1, configFileCommand.size())
    ManagedFile managedScript = configFileCommand.get(0)
    String expectedScriptPath = ".jenkinsShellScript_$expectedJenkinsScriptId"
    assertEquals(expectedJenkinsScriptId, managedScript.getFileId())
    assertEquals(expectedScriptPath, managedScript.getTargetLocation())

    assertEquals("chmod +x $expectedScriptPath".toString(),shellCommands.get(0))
  }

}
