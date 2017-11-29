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
package vars.execMaven

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.tooling.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFileConstants
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOneShellCommand
import static org.junit.Assert.assertEquals

class ExecMavenIT extends LibraryIntegrationTestBase {

  protected String expectedCommand = null

  @Override
  void setUp() throws Exception {
    super.setUp()
  }

  @Test
  void shouldExecuteWithGlobalSettings() {
    expectedCommand = "mvn --global-settings /path/to/workspace@tmp/ssh-or-https-better-match-id"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenGlobalSettingsJob.groovy")
    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithLocalSettings() {
    expectedCommand = "mvn --settings /path/to/workspace@tmp/BETTER_DOMAIN_MVN_SETTINGS"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenLocalSettingsJob.groovy")
    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithDefaults() {
    expectedCommand = "mvn"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenDefaultJob.groovy")
    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithGlobalAndLocalSetting() {
    expectedCommand = "mvn --global-settings /path/to/workspace@tmp/EVEN_BETTER_DOMAIN_MVN_GLOBAL_SETTINGS_ID --settings /path/to/workspace@tmp/EVEN_BETTER_DOMAIN_MVN_SETTINGS"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenGlobalAndLocalSettingsJob.groovy")

    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithCustomConfigVariant1() {

    expectedCommand = "mvn -f path/to/customPom1.xml customGoal1 customGoal2 -B -U -DdefineValue1=true -DdefineFlag1 --global-settings /path/to/workspace@tmp/CUSTOM_GLOBAL_SETTINGS_VARIANT1 --settings /path/to/workspace@tmp/CUSTOM_SETTINGS_VARIANT1"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenCustomVariant1Job.groovy")

    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithCustomConfigVariant2() {

    expectedCommand = "mvn -f path/to/customPom2.xml customGoal3 customGoal4 -B -U -DdefineValue2=true -DdefineFlag2 --global-settings /path/to/workspace@tmp/CUSTOM_GLOBAL_SETTINGS_VARIANT2 --settings /path/to/workspace@tmp/CUSTOM_SETTINGS_VARIANT2"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenCustomVariant2Job.groovy")

    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecMavenWithSettingsViaScmUrlFromEnvJob() {
    this.setEnv(EnvironmentConstants.SCM_URL, "https://subdomain.evenbetterdomain.tld/group1/project2.git")
    expectedCommand = "mvn --global-settings /path/to/workspace@tmp/EVEN_BETTER_DOMAIN_MVN_GLOBAL_SETTINGS_ID --settings /path/to/workspace@tmp/EVEN_BETTER_DOMAIN_MVN_SETTINGS"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenWithSettingsViaScmUrlFromEnvJob.groovy")
    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecMavenWithNPMAndRubyEnvVarsJob() {
    expectedCommand = "mvn"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenWithNPMAndRubyTestJob.groovy")
    assertOneShellCommand(expectedCommand)
    // check if env was set correctly
    assertEquals("/path/to/workspace@tmp/npmrc-project1-id", getEnv(ManagedFileConstants.NPMRC_ENV))
    assertEquals("/path/to/workspace@tmp/npm-user-config-project1-id", getEnv(ManagedFileConstants.NPM_CONFIG_USERCONFIG_ENV))
    assertEquals("/path/to/workspace@tmp/bundle-config-project1-id", getEnv(ManagedFileConstants.BUNDLE_CONFIG_ENV))
  }

  @Test
  void shouldExecuteWithCustomExecutable() {

    expectedCommand = "time /path/to/custom/maven -f path/to/customPom1.xml customGoal1 customGoal2"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenCustomCommandTestJob.groovy")

    assertOneShellCommand(expectedCommand)
  }

  @Test
  void shouldExecuteWithBuildParameters() {
    this.setParams([choiceParam: "choice1", boolParam: true, stringParam: "text"])
    expectedCommand = "mvn clean verify -DchoiceParam=choice1 -DboolParam=true -DstringParam=text --global-settings /path/to/workspace@tmp/EVEN_BETTER_DOMAIN_MVN_GLOBAL_SETTINGS_ID"
    loadAndExecuteScript("vars/execMaven/jobs/execMavenWithBuildParametersTestJob.groovy")

    assertOneShellCommand(expectedCommand)
  }

}
