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
package vars.notify.mattermost

import io.wcm.devops.jenkins.pipeline.model.Result
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert

import static io.wcm.testing.jenkins.pipeline.StepConstants.MATTERMOST_SEND
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce

class MattermostIntegrationTestBase extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
    this.setEnv("BUILD_NUMBER", "2")
    this.setEnv("JOB_NAME", "MOCKED_JOB_NAME")
    this.setEnv("BUILD_URL", "MOCKED_BUILD_URL")
    this.getBinding().setVariable("MATTERMOST_ENDPOINT", "MOCKED_MATTERMOST_ENDPOINT")
  }


  void assertMattermostCall(Result buildResult) {
    Map mattermostCall = assertOnce(MATTERMOST_SEND)
    Assert.assertEquals("jenkins-build-notifications", mattermostCall['channel'].toString())
    Assert.assertEquals("MOCKED_MATTERMOST_ENDPOINT", mattermostCall['endpoint'].toString())
    Assert.assertEquals(buildResult.getColor(), mattermostCall['color'].toString())
    Assert.assertEquals("${buildResult.toString()} - MOCKED_JOB_NAME 2\n(<MOCKED_BUILD_URL|📝 Open>) (<MOCKED_BUILD_URLconsole/|🔍 Open log>)".toString(), mattermostCall['message'].toString())
                                                                      
  }
}
