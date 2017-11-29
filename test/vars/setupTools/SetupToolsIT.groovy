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
package vars.setupTools

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.tooling.jenkins.pipeline.model.Tool
import org.hamcrest.CoreMatchers
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class SetupToolsIT extends LibraryIntegrationTestBase {

  @Test
  void shouldUseCustomEnvVars() {
    String expectedMavenPath = TOOL_MAVEN_PREFIX.concat(TOOL_MAVEN)
    String expectedJdkPath = TOOL_JDK_PREFIX.concat(TOOL_JDK)
    loadAndExecuteScript("vars/setupTools/jobs/shouldUseCustomEnvVarsTestJob.groovy")

    assertEquals(expectedMavenPath, this.getEnv("customMavenEnvVar"))
    assertEquals(expectedJdkPath, this.getEnv("customJdkEnvVar"))
    assertThat(this.getEnv("PATH"), CoreMatchers.containsString(expectedMavenPath))
    assertThat(this.getEnv("PATH"), CoreMatchers.containsString(expectedJdkPath))
  }

  @Test
  void shouldUseDefaultEnvVars() {
    String expectedMavenPath = TOOL_MAVEN_PREFIX.concat(TOOL_MAVEN)
    String expectedJdkPath = TOOL_JDK_PREFIX.concat(TOOL_JDK)
    loadAndExecuteScript("vars/setupTools/jobs/shouldUseDefaultEnvVarsTestJob.groovy")

    assertEquals(expectedMavenPath, this.getEnv(Tool.MAVEN.getEnvVar()))
    assertEquals(expectedJdkPath, this.getEnv(Tool.JDK.getEnvVar()))
    assertThat(this.getEnv("PATH"), CoreMatchers.containsString(expectedMavenPath))
    assertThat(this.getEnv("PATH"), CoreMatchers.containsString(expectedJdkPath))
  }

  @Test(expected = AbortException.class)
  void shouldFailWhenMavenNotFound() {
    try {
      def script = loadScript("vars/setupTools/jobs/shouldFailWhenToolNotFound.groovy")
      script.execute()
    } catch (AbortException e) {
      assertThat(e.getMessage(), CoreMatchers.containsString('invalid-maven-tool'))
      throw e
    } catch (Exception e1) {
      e1.printStackTrace()
      dslMock.printLogMessages()
    }
  }

}
