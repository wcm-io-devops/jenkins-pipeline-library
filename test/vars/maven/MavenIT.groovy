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
package vars.maven

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertStepCalls
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertTwice
import static org.junit.Assert.assertEquals

class MavenIT extends LibraryIntegrationTestBase {

  @Test
  void purgeSnapshotsFromRepositoryCustom() {
    Object scriptResult = loadAndExecuteScript("vars/maven/jobs/purgeSnapshotsCustomTestJob.groovy")
    List shellCommands = assertStepCalls(SH,3)

    Map expectedManagedScriptShellCommand = [
        'script': "./.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh --repo='custom/path/to/repo' --dryrun --loglvl=8"
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)
  }

  @Test
  void purgeSnapshotsFromRepositoryCustomMap() {
    Object scriptResult = loadAndExecuteScript("vars/maven/jobs/purgeSnapshotsCustomMapTestJob.groovy")
    List shellCommands = assertStepCalls(SH,3)

    Map expectedManagedScriptShellCommand = [
        'script': "./.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh --repo='custom/path/to/repo/from/map' --dryrun --loglvl=2"
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)
  }

  @Test
  void purgeSnapshotsFromRepositoryDefaults() {
    Object scriptResult = loadAndExecuteScript("vars/maven/jobs/purgeSnapshotsDefaultsTestJob.groovy")
    List shellCommands = assertStepCalls(SH,3)

    Map expectedManagedScriptShellCommand = [
        'script': "./.libraryShellScript_jenkinsPipelineLibrary___managedScripts___shell___maven___purge-snapshots.sh"
    ]
    Map actualManagedScriptShellCommand = (Map) shellCommands[1]
    assertEquals(expectedManagedScriptShellCommand, actualManagedScriptShellCommand)
  }

}
