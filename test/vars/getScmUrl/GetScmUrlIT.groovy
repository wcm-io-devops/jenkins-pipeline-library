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
package vars.getScmUrl

import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert
import org.junit.Test

class GetScmUrlIT extends LibraryIntegrationTestBase {

  @Test
  void shouldUseScmConfig() {
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlFromConfigTestJob.groovy")
    Assert.assertEquals("scm-url-from-scm-config", actualScmUrl)
  }

  @Test
  void shouldUseScmEnvVar() {
    this.setEnv(EnvironmentConstants.SCM_URL, "scm-url-from-env-var")
    this.setEnv(EnvironmentConstants.GIT_URL, "git-url-from-env-var")
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlFromEnvVarTestJob.groovy")
    Assert.assertEquals("scm-url-from-env-var", actualScmUrl)
  }

  @Test
  void shouldFallbackToGitUrlEnvVar() {
    this.setEnv(EnvironmentConstants.GIT_URL, "git-url-from-env-var")
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlFromEnvVarTestJob.groovy")
    Assert.assertEquals("git-url-from-env-var", actualScmUrl)
  }

  @Test
  void shouldFallbackToGitUrl1EnvVar() {
    this.setEnv(EnvironmentConstants.GIT_URL_1, "git-url-1-from-env-var")
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlFromEnvVarTestJob.groovy")
    Assert.assertEquals("git-url-1-from-env-var", actualScmUrl)
  }

  @Test
  void shouldFallbackToJobName() {
    this.setEnv(EnvironmentConstants.JOB_NAME, "job-name-from-env-var")
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlJobNameFallbackTestJob.groovy", [ fallback: true ])
    Assert.assertEquals("job-name-from-env-var", actualScmUrl)
  }

  @Test
  void shouldNotFallbackToJobName() {
    this.setEnv(EnvironmentConstants.JOB_NAME, "job-name-from-env-var")
    String actualScmUrl = loadAndExecuteScript("vars/getScmUrl/jobs/getScmUrlJobNameFallbackTestJob.groovy", [ fallback: false ])
    Assert.assertNull(actualScmUrl)
  }
}
