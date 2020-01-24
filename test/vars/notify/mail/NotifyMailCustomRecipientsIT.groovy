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
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import static io.wcm.testing.jenkins.pipeline.StepConstants.EMAILEXT
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static org.junit.Assert.assertEquals

class NotifyMailCustomRecipientsIT extends LibraryIntegrationTestBase {

  @Test
  void shouldCustomNotifyOnSuccess() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('Subject NOTIFY_ON_SUCCESS', extmailCall[NOTIFY_SUBJECT])
    assertEquals('Body NOTIFY_ON_SUCCESS', extmailCall[NOTIFY_BODY])
    assertEquals('build-success@example.org', extmailCall[NOTIFY_TO])
    assertEquals([
      [$class: 'CulpritsRecipientProvider'],
      [$class: 'RequesterRecipientProvider']
    ], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldDefaultNotifyOnAbort() {
    this.context.getRunWrapperMock().setResult(Result.ABORTED.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('default-recipient@example.com', extmailCall[NOTIFY_TO])
    assertEquals([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldNotNotifyOnNotBuild() {
    this.context.getRunWrapperMock().setResult(Result.NOT_BUILT.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertNone(EMAILEXT)
  }

  @Test
  void shouldCustomNotifyOnFixed() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('build-fixed@example.org', extmailCall[NOTIFY_TO])
    assertEquals([
      [$class: 'CulpritsRecipientProvider'],
      [$class: 'RequesterRecipientProvider'],
    ], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldCustomNotifyOnUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.SUCCESS.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('build-unstable@example.org', extmailCall[NOTIFY_TO])
    assertEquals([
      [$class: 'CulpritsRecipientProvider'],
      [$class: 'RequesterRecipientProvider'],
      [$class: 'UpstreamComitterRecipientProvider'],
    ], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldDefaultNotifyOnStillUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('default-recipient@example.com', extmailCall[NOTIFY_TO])
    assertEquals([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldCustomNotifyOnFailure() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('Subject NOTIFY_ON_FAILURE', extmailCall[NOTIFY_SUBJECT])
    assertEquals('Body NOTIFY_ON_FAILURE', extmailCall[NOTIFY_BODY])
    assertEquals('build-failure@example.org', extmailCall[NOTIFY_TO])
    assertEquals([
      [$class: 'CulpritsRecipientProvider'],
      [$class: 'RequesterRecipientProvider'],
      [$class: 'DevelopersRecipientProvider'],
    ], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  @Test
  void shouldCustomNotifyOnStillFailing() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailCustomRecipientsJob.groovy")
    Map extmailCall = assertOnce(EMAILEXT)

    assertEquals('build-still-failing@example.org', extmailCall[NOTIFY_TO])
    assertEquals([
      [$class: 'CulpritsRecipientProvider'],
      [$class: 'RequesterRecipientProvider'],
      [$class: 'FirstFailingBuildSuspectsRecipientProvider']
    ], extmailCall[NOTIFY_RECIPIENT_PROVIDERS])

    commonAssertions(extmailCall)
  }

  void commonAssertions(Map extmailCall) {
    assertEquals("attachmentsPattern is wrong", 'custom/pattern/**/*.txt', extmailCall[NOTIFY_ATTACHMENTS_PATTERN] ?: 'attachmentsPatternNotSet')
    assertEquals("attachLog is wrong", true, extmailCall[NOTIFY_ATTACH_LOG] ?: 'attachLogNotSet')
    assertEquals("compressLog is wrong", true, extmailCall[NOTIFY_COMPRESS_LOG] ?: 'compressLogNotSet')
    assertEquals("mimeType is wrong", 'text/html', extmailCall[NOTIFY_MIME_TYPE] ?: 'mimeTypeNotSet')
  }
}
