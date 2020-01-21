/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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

class NotifyMailDisabledIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.setEnv("BUILD_NUMBER", "2")
    this.setEnv("GIT_BRANCH", "DETECTED_GIT_BRANCH")
  }

  @Test
  void shouldNotNotifyOnSuccess() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnAbort() {
    this.context.getRunWrapperMock().setResult(Result.ABORTED.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnNotBuild() {
    this.context.getRunWrapperMock().setResult(Result.NOT_BUILT.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFixed() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.SUCCESS.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnStillUnstable() {
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnFailure() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }

  @Test
  void shouldNotNotifyOnStillFailing() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/mail/jobs/notifyMailDisabledTestJob.groovy")
    assertNone(EMAILEXT)
  }
}
