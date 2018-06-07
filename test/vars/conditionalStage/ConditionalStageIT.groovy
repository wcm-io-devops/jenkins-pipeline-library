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

package vars.conditionalStage

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.junit.Assert
import org.junit.Test

class ConditionalStageIT extends LibraryIntegrationTestBase {

  @Test
  void shouldNotRunConditionalStage() {
    NullPointerException expectedException = null
    try {
      loadAndExecuteScript("vars/conditionalStage/jobs/shouldNotRunConditionalStageTestJob.groovy")
    } catch (NullPointerException ex) {
      expectedException = ex
    }
    Assert.assertNotNull(expectedException)
    StepRecorderAssert.assertNone(StepConstants.SH)
  }

  @Test
  void shouldRunConditionalStage() {
    loadAndExecuteScript("vars/conditionalStage/jobs/shouldRunConditionalStageTestJob.groovy")
    StepRecorderAssert.assertOnce(StepConstants.SH)
  }

}
