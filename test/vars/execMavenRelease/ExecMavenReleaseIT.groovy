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
package vars.execMavenRelease

import hudson.AbortException
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOneShellCommand
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertTwice
import static org.junit.Assert.assertEquals

class ExecMavenReleaseIT extends LibraryIntegrationTestBase {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none()

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.context.getDslMock().mockResource("effective-pom.tmp", "mavenRelease/valid-effective-pom.xml")
  }

  @Test
  void shouldFailWhenScmUrlIsNull() throws AbortException {
    expectedEx.expect(AbortException.class)
    expectedEx.expectMessage("Unable to retrieve SCM url")

    this.setEnv(EnvironmentConstants.SCM_URL, null)

    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldFailWhenScmUrlIsNullTestJob.groovy")
    assertNone(StepConstants.SH)
    assertNone(StepConstants.SSH_AGENT)
  }

  @Test
  void shouldFailWhenScmUrlIsNotGitSSH() {
    expectedEx.expect(AbortException.class)
    expectedEx.expectMessage("Invalid SCM url")

    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldFailWhenScmUrlIsNotGitSSHTestJob.groovy")
    assertNone(StepConstants.SH)
    assertNone(StepConstants.SSH_AGENT)
  }

  @Test
  void shouldFailWithNoBranchEnvVar() {
    expectedEx.expect(AbortException.class)
    expectedEx.expectMessage("Unable to retrieve 'GIT_BRANCH' environment variable")

    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldFailWithNoBranchEnvVarTestJob.groovy")
    assertNone(StepConstants.SH)
    assertNone(StepConstants.SSH_AGENT)
  }

  @Test
  void shouldFailWithNotAllowedBranchName() {
    expectedEx.expect(AbortException.class)
    expectedEx.expectMessage("Not allowed branch detected.")

    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldFailWithNotAllowedBranchNameTestJob.groovy")
    assertNone(StepConstants.SH)
    assertNone(StepConstants.SSH_AGENT)
  }

  @Test
  void shouldFailDueToWrongMavenReleasePluginVersion() {
    this.context.getDslMock().mockResource("effective-pom.tmp", "mavenRelease/invalid-maven-release-version-pom.xml")
    expectedEx.expect(AbortException.class)
    expectedEx.expectMessage("org.apache.maven.plugins:maven-release-plugin version requirement not met.")

    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldExecMavenReleaseWithKeyAgentTestJob.groovy")

    // assert that the sshagent step was called once
    List keyAgentCredentialList = (List) assertOnce(StepConstants.SSH_AGENT)
    assertEquals("provided ssh credentials are wrong", ['ssh-git-push-credentials-id'], keyAgentCredentialList)

    List shellCommands = assertTwice(StepConstants.SH)
    assertEquals(["mvn help:effective-pom -B -U -Doutput=effective-pom.tmp", "mvn release:prepare release:perform -B -U"], shellCommands)
  }

  /*@Test
  void shouldFailDueToWrongScmProviderGitExeVersion() {
      this.dslMock.mockResource("effective-pom.tmp","mavenRelease/invalid-maven-scm-provider-gitexe-pom.xml")

      loadAndExecuteScript("vars/execMavenRelease/jobs/shouldExecMavenReleaseWithKeyAgentTestJob.groovy")
      expectedEx.expect(AbortException.class)
      expectedEx.expectMessage("org.apache.maven.scm:maven-scm-provider-gitexe versionNumber requirement not met.")

      // assert that the sshagent step was called once
      List keyAgentCredentialList = (List) assertOnce(StepConstants.SSH_AGENT)
      assertEquals("provided ssh credentials are wrong", ['ssh-git-push-credentials-id'], keyAgentCredentialList)

      List shellCommands = assertTwice(StepConstants.SH)
      assertEquals(["mvn help:effective-pom -B -U -Doutput=effective-pom.tmp", "mvn release:prepare release:perform -B -U"], shellCommands)
  }*/

  @Test
  void shouldExecMavenReleaseWithKeyAgent() {
    loadAndExecuteScript("vars/execMavenRelease/jobs/shouldExecMavenReleaseWithKeyAgentTestJob.groovy")

    // assert that the sshagent step was called once
    List keyAgentCredentialList = (List) assertOnce(StepConstants.SSH_AGENT)
    assertEquals("provided ssh credentials are wrong", ['ssh-git-push-credentials-id'], keyAgentCredentialList)

    List shellCommands = assertTwice(StepConstants.SH)
    assertEquals(["mvn help:effective-pom -B -U -Doutput=effective-pom.tmp", "mvn release:prepare release:perform -B -U"], shellCommands)
  }

}
