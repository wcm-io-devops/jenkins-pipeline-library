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
package vars.execNpm

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFileConstants
import org.junit.Assert
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOneShellCommand

class ExecNpmIT extends LibraryIntegrationTestBase {

  protected String expectedCommand = null

  @Test
  void shouldRunWithDefaults() {
    expectedCommand = "npm"
    loadAndExecuteScript("vars/execNpm/jobs/execNpmDefaultTestJob.groovy")
    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldRunWithCustomAndAutoLookup() {
    expectedCommand = "/path/to/custom/npm run build -flag --property=value --userconfig /path/to/workspace@tmp/npm-user-config-id --globalconfig /path/to/workspace@tmp/npmrc-id"
    loadAndExecuteScript("vars/execNpm/jobs/execNpmCustomAndAutoLookupTestJob.groovy")
    assertOneShellCommand(expectedCommand)
    // check if npm config userconfig was automatically provived by setting environment variable
    Assert.assertEquals(WORKSPACE_TMP_PATH.concat("npm-user-config-id"), getEnv(ManagedFileConstants.NPM_CONF_USERCONFIG_ENV))
  }
}
