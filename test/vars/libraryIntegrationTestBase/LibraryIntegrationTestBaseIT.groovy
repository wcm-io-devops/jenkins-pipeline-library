/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package vars.libraryIntegrationTestBase

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.jenkinsci.plugins.pipeline.utility.steps.fs.FileWrapper;
import org.junit.Assert;
import org.junit.Test

import java.nio.file.Path

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH;

class LibraryIntegrationTestBaseIT extends LibraryIntegrationTestBase {

  Integer currentRetry = -1

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.currentRetry = -1
  }

  @Test
  void shouldFindOneFile() {
    FileWrapper[] result = loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/shouldFindOneFileTestJob.groovy")

    Assert.assertEquals(1, result.size())
    Assert.assertEquals("invalid.json", result[0].getName())
    Assert.assertEquals(false, result[0].isDirectory())

  }

  @Test
  void shouldFindNoFile() {
    FileWrapper[] result = loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/shouldFindNoFileTestJob.groovy")
    Assert.assertEquals(0, result.size())
  }

  @Test
  void shouldFindMultipleFiles() {
    FileWrapper[] result = loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/shouldFindMultipleFilesTestJob.groovy")
    Assert.assertEquals(4, result.size())

    // resources/credentials/http/credentials.json
    FileWrapper underTest = result[0]
    Assert.assertEquals("credentials.json", underTest.getName())
    Assert.assertEquals(false, underTest.isDirectory())
    Assert.assertEquals(new File("test/resources/credentials/http/credentials.json").getAbsolutePath(), underTest.getPath())

    // resources/credentials/parser-test.json
    underTest = result[1]
    Assert.assertEquals("parser-test.json", underTest.getName())
    Assert.assertEquals(false, underTest.isDirectory())
    Assert.assertEquals(new File("test/resources/credentials/parser-test.json").getAbsolutePath(), underTest.getPath())

    // resources/credentials/scm/credentials.json
    underTest = result[2]
    Assert.assertEquals("credentials.json", underTest.getName())
    Assert.assertEquals(false, underTest.isDirectory())
    Assert.assertEquals(new File("test/resources/credentials/scm/credentials.json").getAbsolutePath(), underTest.getPath())

    // resources/credentials/ssh/credentials.json
    underTest = result[3]
    Assert.assertEquals("credentials.json", underTest.getName())
    Assert.assertEquals(false, underTest.isDirectory())
    Assert.assertEquals(new File("test/resources/credentials/ssh/credentials.json").getAbsolutePath(), underTest.getPath())
  }

  @Test
  void shouldFindDescendandsWithoutGlob() {
    FileWrapper[] result = loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/shouldFindDescendandsWithoutGlobTestJob.groovy")
    Assert.assertTrue(result.size() > 0)
  }

  @Test
  void shouldNotRetryWhenNothingFailed() {
    loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/retryTestJob.groovy")

    StepRecorderAssert.assertOnce(StepConstants.RETRY)
    List actualShellCalls = StepRecorderAssert.assertStepCalls(StepConstants.SH, 1)
    List expectedShellCalls = [
      "some_randomly_failing_command",
    ]

    Assert.assertEquals(expectedShellCalls, actualShellCalls)
  }

  @Test(expected = AbortException)
  void shouldFailWhenMaxRetriesAreReached() {
    helper.registerAllowedMethod(SH, [String.class], shellAlwaysFailing)

    loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/retryTestJob.groovy")

    StepRecorderAssert.assertOnce(StepConstants.RETRY)
    List actualShellCalls = StepRecorderAssert.assertStepCalls(StepConstants.SH, 3)
    List expectedShellCalls = [
      "some_randomly_failing_command",
      "some_randomly_failing_command",
      "some_randomly_failing_command",
    ]

    Assert.assertEquals(expectedShellCalls, actualShellCalls)
  }

  @Test
  void shouldStopRetryOnSuccessfullCommand() {
    helper.registerAllowedMethod(SH, [String.class], shellFailingOnce)

    loadAndExecuteScript("vars/libraryIntegrationTestBase/jobs/retryTestJob.groovy")

    StepRecorderAssert.assertOnce(StepConstants.RETRY)
    List actualShellCalls = StepRecorderAssert.assertStepCalls(StepConstants.SH, 2)
    List expectedShellCalls = [
      "some_randomly_failing_command",
      "some_randomly_failing_command",
    ]

    Assert.assertEquals(expectedShellCalls, actualShellCalls)
  }

  def shellAlwaysFailing = { String incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
    throw new AbortException("I am failing!")
  }

  def shellFailingOnce = { String incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
    this.currentRetry = currentRetry+1

    if (currentRetry < 1) {
      throw new AbortException("I am failing!")
    }
  }
}
