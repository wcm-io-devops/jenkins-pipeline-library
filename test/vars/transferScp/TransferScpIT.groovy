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
package vars.transferScp

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOneShellCommand
import static org.junit.Assert.assertEquals

class TransferScpIT extends LibraryIntegrationTestBase {


  @Test
  void shouldTransferSingleWithCredentials() {
    String expectedCommand = 'scp -P 22 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null /path/to/source testserveruser@testserver1.testservers.domain.tld:"/path/to/destination"'
    loadAndExecuteScript("vars/transferScp/jobs/transferScpSingleTestJob.groovy")
    assertOneShellCommand(expectedCommand)
    List keyAgentCredentialList = assertOnce(StepConstants.SSH_AGENT)
    assertEquals("provided ssh credentials are wrong", ['ssh-key-for-testservers'], keyAgentCredentialList)
  }

  @Test
  void shouldTransferRecursiveWithoutCredentials() {
    String expectedCommand = '/usr/bin/scp -C -4 -P 2222 -r /path/to/recursive\\ source/* testuser@subdomain.domain.tld:"/path/to/recursive\\ destination"'
    loadAndExecuteScript("vars/transferScp/jobs/transferScpRecursiveTestJob.groovy")
    assertOneShellCommand(expectedCommand)
    List keyAgentCredentialList = assertOnce(StepConstants.SSH_AGENT)
    assertEquals("provided ssh credentials are wrong", [], keyAgentCredentialList)
  }

  @Test
  void shouldTransferWithMinimalConfiguration() {
    String expectedCommand = 'scp -P 22 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null /path/to/minimal\\ source minimal.domain.tld:"/path/to/minimal\\ destination"'
    loadAndExecuteScript("vars/transferScp/jobs/transferScpWithMinimalConfiguration.groovy")
    assertOneShellCommand(expectedCommand)
    List keyAgentCredentialList = assertOnce(StepConstants.SSH_AGENT)
    assertEquals("provided ssh credentials are wrong", [], keyAgentCredentialList)
  }

}
