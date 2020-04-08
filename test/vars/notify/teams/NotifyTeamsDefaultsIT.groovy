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
package vars.notify.teams

import io.wcm.devops.jenkins.pipeline.model.Result
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.OFFICE365_CONNECTOR_SEND
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertNone

class NotifyTeamsDefaultsIT extends NotifyTeamsIntegrationTestBase {

  @Test
  void shouldNotNotifyOnSuccess() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertNone(OFFICE365_CONNECTOR_SEND)
  }

  @Test
  void shouldNotNotifyOnAbort() {
    this.context.getRunWrapperMock().setResult(Result.ABORTED.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertNone(OFFICE365_CONNECTOR_SEND)
  }

  @Test
  void shouldNotNotifyOnNotBuild() {
    this.context.getRunWrapperMock().setResult(Result.NOT_BUILT.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertNone(OFFICE365_CONNECTOR_SEND)
  }

  @Test
  void shouldNotifyOnFixed() {
    this.context.getRunWrapperMock().setResult(Result.SUCCESS.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertTeamsCall(Result.FIXED)
  }

  @Test
  void shouldNotifyOnUnstable() {
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertTeamsCall(Result.UNSTABLE)
  }

  @Test
  void shouldNotifyOnStillUnstable() {
    this.context.getRunWrapperMock().setResult(Result.UNSTABLE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.UNSTABLE.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertTeamsCall(Result.STILL_UNSTABLE)
  }

  @Test
  void shouldNotifyOnFailure() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertTeamsCall(Result.FAILURE)
  }

  @Test
  void shouldNotifyOnStillFailing() {
    this.context.getRunWrapperMock().setResult(Result.FAILURE.toString())
    this.context.getRunWrapperMock().setPreviousBuildResult(Result.FAILURE.toString())
    loadAndExecuteScript("vars/notify/teams/jobs/notifyTeamsDefaultsJob.groovy")
    assertTeamsCall(Result.STILL_FAILING)
  }

}
