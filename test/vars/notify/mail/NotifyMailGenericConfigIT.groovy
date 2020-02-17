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
package vars.notify.mail

import hudson.model.Result
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import static io.wcm.testing.jenkins.pipeline.StepConstants.EMAILEXT
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static org.junit.Assert.assertEquals

class NotifyMailGenericConfigIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.setEnv(EnvironmentConstants.JOB_NAME, "team-a/project1")
  }

  @Test
  void shouldNotifyOnSuccess() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals("subject is wrong", 'successSubject', extmailCall[NOTIFY_SUBJECT])
    assertEquals("body is wrong", 'successBody', extmailCall[NOTIFY_BODY])
    assertEquals("attachmentsPattern is wrong", 'successAttachmentsPattern', extmailCall[NOTIFY_ATTACHMENTS_PATTERN])
    assertEquals("attachLog is wrong", true, extmailCall[NOTIFY_ATTACH_LOG])
    assertEquals("compressLog is wrong", true, extmailCall[NOTIFY_COMPRESS_LOG])
    assertEquals("mimeType is wrong", null, extmailCall[NOTIFY_MIME_TYPE])
    assertEquals("to is wrong", 'success@company.tld', extmailCall[NOTIFY_TO])


    String expectedRecipientProviderList = '[[$class:CulpritsRecipientProvider]]'

    assertEquals("expectedRecipientProviderList is wrong", expectedRecipientProviderList, extmailCall[NOTIFY_RECIPIENT_PROVIDERS] ? extmailCall[NOTIFY_RECIPIENT_PROVIDERS].toString() : 'recipientProvidersNotSet')
  }

  @Test
  void shouldNotNotifyOnAbort() {
    this.context.getRunWrapperMock().setResult(Result.ABORTED.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnNotBuild() {
    this.context.getRunWrapperMock().setResult(Result.NOT_BUILT.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFixed() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnUnstable() {
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnStillUnstable() {
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFailure() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")

    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals("subject is wrong", 'subjectFromGenericConfig', extmailCall[NOTIFY_SUBJECT])
    assertEquals("body is wrong", 'genericConfigBody', extmailCall[NOTIFY_BODY])
    assertEquals("attachmentsPattern is wrong", 'patternFromGenericConfig', extmailCall[NOTIFY_ATTACHMENTS_PATTERN])
    assertEquals("attachLog is wrong", true, extmailCall[NOTIFY_ATTACH_LOG])
    assertEquals("compressLog is wrong", false, extmailCall[NOTIFY_COMPRESS_LOG])
    assertEquals("mimeType is wrong", null, extmailCall[NOTIFY_MIME_TYPE])
    assertEquals("to is wrong", 'generic-config-mail@company.tld', extmailCall[NOTIFY_TO])


    String expectedRecipientProviderList = '[[$class:CulpritsRecipientProvider], [$class:RequesterRecipientProvider]]'

    assertEquals("expectedRecipientProviderList is wrong", expectedRecipientProviderList, extmailCall[NOTIFY_RECIPIENT_PROVIDERS] ? extmailCall[NOTIFY_RECIPIENT_PROVIDERS].toString() : 'recipientProvidersNotSet')
  }

  @Test
  void shouldNotNotifyOnStillFailing() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDefaultsJob.groovy")
    assertNone(EMAILEXT)
  }

}
