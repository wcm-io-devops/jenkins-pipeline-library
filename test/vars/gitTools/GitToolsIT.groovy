package vars.gitTools

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH

class GitToolsIT extends LibraryIntegrationTestBase {

  List<Map> mockedShellCommands = []

  Boolean repoExists = false

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
  }

  @Test
  void shouldReturnFetchOriginWithParam() {
    String remotes = "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
      "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
    String expectedFetchOrigin = "https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git"
    String actualFetchOrigin = loadAndExecuteScript("vars/gitTools/jobs/getFetchOriginTestJob.groovy", [ remotes: remotes])
    Assert.assertEquals(expectedFetchOrigin, actualFetchOrigin)
  }

  @Test
  void shouldReturnFetchOriginWithGetRemotes() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    String expectedFetchOrigin = "git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git"
    String actualFetchOrigin = loadAndExecuteScript("vars/gitTools/jobs/getFetchOriginTestJob.groovy")
    Assert.assertEquals(expectedFetchOrigin, actualFetchOrigin)
  }

  @Test
  void shouldReturnPushOriginWithParam() {
    String remotes = "origin  https://host1user@host1.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
      "origin  https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git (push)"
    String expectedPushOrigin = "https://host2user@host2.domain.tld/wcm-io-devops/jenkins-pipeline-library.git"
    String actualPushOrigin = loadAndExecuteScript("vars/gitTools/jobs/getPushOriginTestJob.groovy", [ remotes: remotes])
    Assert.assertEquals(expectedPushOrigin, actualPushOrigin)
  }

  @Test
  void shouldReturnPushOriginWithGetRemotes() {
    mockedShellCommands = [
      [
        script: "git remote -v",
        result: "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (fetch)\n" +
          "origin  git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git (push)"
      ]
    ]
    String expectedPushOrigin = "git@host1.domain.tld:wcm-io-devops/jenkins-pipeline-library.git"
    String actualPushOrigin = loadAndExecuteScript("vars/gitTools/jobs/getPushOriginTestJob.groovy")
    Assert.assertEquals(expectedPushOrigin, actualPushOrigin)
  }

  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
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
