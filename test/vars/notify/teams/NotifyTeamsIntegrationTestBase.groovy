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
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert

import static io.wcm.testing.jenkins.pipeline.StepConstants.MATTERMOST_SEND
import static io.wcm.testing.jenkins.pipeline.StepConstants.OFFICE365_CONNECTOR_SEND
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce

class NotifyTeamsIntegrationTestBase extends LibraryIntegrationTestBase {

  String expectedMessage = null

  void assertTeamsCall(Result buildResult) {
    Map teamsCall = assertOnce(OFFICE365_CONNECTOR_SEND)
    Assert.assertEquals(buildResult.getColor(), teamsCall['color'].toString())
    if (expectedMessage != null) {
      Assert.assertEquals(expectedMessage, teamsCall['message'].toString())
    }

  }
}
