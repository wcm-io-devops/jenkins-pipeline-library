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
package vars.managedScripts

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH
import static io.wcm.testing.jenkins.pipeline.StepConstants.WRITE_FILE
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertStepCalls
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertTwice
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MangedPipelineShellScriptIT extends LibraryIntegrationTestBase {

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
  }

  @Test
  void shouldExecutePipelineShellScriptWithoutCommandBuilder() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/pipelineShellScript/shouldExecutePipelineShellScriptWithoutCommandBuilderTestJob.groovy")
    Map writeFileCommand = assertOnce(WRITE_FILE)
    List shellCommands = assertStepCalls(SH,3 )

    commonAssertions(writeFileCommand, shellCommands)


    Map expectedManagedScriptShellCommand = [
      'script': './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertNull(scriptResult)
  }

  @Test
  void shouldExecutePipelineShellScriptWithDefaults() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/pipelineShellScript/shouldExecutePipelineShellScriptWithDefaultsTestJob.groovy")
    Map writeFileCommand = assertOnce(WRITE_FILE)
    List shellCommands = assertStepCalls(SH,3 )

    commonAssertions(writeFileCommand, shellCommands)


    Map expectedManagedScriptShellCommand = [
      'script': './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertNull(scriptResult)
  }

  @Test
  void shouldExecutePipelineShellScriptWithReturnStatus() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/pipelineShellScript/shouldExecutePipelineShellScriptWithReturnStatusTestJob.groovy")
    Map writeFileCommand = assertOnce(WRITE_FILE)
    List shellCommands = assertStepCalls(SH,3 )

    commonAssertions(writeFileCommand, shellCommands)

    Map expectedManagedScriptShellCommand = [
      'returnStatus' : true,
      'script': './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo arg1 arg2'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals(137,scriptResult)
  }

  @Test
  void shouldExecutePipelineShellScriptWithReturnStdoutStatus() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/pipelineShellScript/shouldExecutePipelineShellScriptWithReturnStdoutStatusTestJob.groovy")
    Map writeFileCommand = assertOnce(WRITE_FILE)
    List shellCommands = assertStepCalls(SH,3 )

    commonAssertions(writeFileCommand, shellCommands)

    Map expectedManagedScriptShellCommand = [
      'returnStdout' : true,
      'script': './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo argName argValue'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals("hello world",scriptResult)
  }

  @Test
  void shouldExecutePipelineShellScriptWithReturnStdout() {
    Object scriptResult = loadAndExecuteScript("vars/managedScripts/jobs/pipelineShellScript/shouldExecutePipelineShellScriptWithReturnStdoutTestJob.groovy")
    Map writeFileCommand = assertOnce(WRITE_FILE)
    List shellCommands = assertStepCalls(SH,3 )

    commonAssertions(writeFileCommand, shellCommands)

    Map expectedManagedScriptShellCommand = [
      'returnStdout' : true,
      'script': './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo arg3 arg4'
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)

    assertEquals('hello world',scriptResult)
  }

  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    Boolean returnStatus = incomingCommand.returnStatus ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo argName argValue':
          return "hello world\n"
        break
        case './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo arg3 arg4':
          return "hello world\n\t     \n"
          break
        default:
          return ""
      }
    }
    if (returnStatus) {
      switch (script) {
        case './.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh path/to/repo arg1 arg2':
          return 137
        break
        default:
          return -1
      }
    }
    return null
  }

  void commonAssertions(Map writeFileCommand, List shellCommands) {
    assertEquals("UTF-8", writeFileCommand.getOrDefault('encoding', null))
    assertEquals(".libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh", writeFileCommand.getOrDefault('file', null))
    assertEquals("#!/bin/bash\n" +
      "echo \"hello world\"\n" +
      "", writeFileCommand.getOrDefault('text', null))

    assertEquals("chmod +x .libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh",shellCommands.get(0))
    assertEquals("rm .libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh",shellCommands.get(2))
  }

}
