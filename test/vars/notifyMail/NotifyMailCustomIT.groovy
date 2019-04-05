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
package vars.notifyMail

import hudson.model.Result
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.EMAILEXT
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import static org.junit.Assert.assertEquals

class NotifyMailCustomIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.setEnv("BUILD_NUMBER", "2")
    this.setEnv("GIT_BRANCH", "DETECTED_GIT_BRANCH")
  }

  @Test
  void shouldNotifyOnSuccess() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)
    assertCorrectExtmailCall(extmailCall)
  }

  @Test
  void shouldNotifyOnAbort() {
    this.context.getRunWrapperMock().setResult(Result.ABORTED.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertOnce(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnNotBuild() {
    this.context.getRunWrapperMock().setResult(Result.NOT_BUILT.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFixed() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.SUCCESS.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnStillUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFailure() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnStillFailing() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notifyMail/jobs/notifyMailCustomJob.groovy")
    assertNone(EMAILEXT)
  }

  void assertCorrectExtmailCall(Map extmailCall) {
    assertEquals("subject is wrong", 'custom mail subject with trigger: SUCCESS', extmailCall[NOTIFY_SUBJECT] ?: 'subjectNotSet')
    assertEquals("body is wrong", 'custom body with trigger: SUCCESS', extmailCall[NOTIFY_BODY] ?: 'bodyNotSet')
    assertEquals("attachmentsPattern is wrong", 'custom/pattern/**/*.txt', extmailCall[NOTIFY_ATTACHMENTS_PATTERN] ?: 'attachmentsPatternNotSet')
    assertEquals("attachLog is wrong", true, extmailCall[NOTIFY_ATTACH_LOG] ?: 'attachLogNotSet')
    assertEquals("compressLog is wrong", true, extmailCall[NOTIFY_COMPRESS_LOG] ?: 'compressLogNotSet')
    assertEquals("mimeType is wrong", 'text/html', extmailCall[NOTIFY_MIME_TYPE] ?: 'mimeTypeNotSet')
    assertEquals("to is wrong", 'test@test.com', extmailCall[NOTIFY_TO] ?: 'toNotSet')


    String expectedRecipientProviderList = '[[$class:CulpritsRecipientProvider], [$class:DevelopersRecipientProvider], [$class:FirstFailingBuildSuspectsRecipientProvider], [$class:RequesterRecipientProvider], [$class:UpstreamComitterRecipientProvider]]'

    assertEquals("expectedRecipientProviderList is wrong", expectedRecipientProviderList, extmailCall[NOTIFY_RECIPIENT_PROVIDERS] ? extmailCall[NOTIFY_RECIPIENT_PROVIDERS].toString() : 'recipientProvidersNotSet')
  }
}
