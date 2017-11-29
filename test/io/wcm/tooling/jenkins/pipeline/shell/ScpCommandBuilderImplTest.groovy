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
package io.wcm.tooling.jenkins.pipeline.shell

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import io.wcm.tooling.jenkins.pipeline.credentials.Credential
import org.junit.Before
import org.junit.Test

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class ScpCommandBuilderImplTest extends DSLTestBase {

  ScpCommandBuilderImpl underTest

  Map configTemplate = [
      (SCP_HOST)          : "testhost",
      (SCP_PORT)          : null,
      (SCP_USER)          : null,
      (SCP_ARGUMENTS)     : [],
      (SCP_RECURSIVE)     : false,
      (SCP_SOURCE)        : "/path/to/source/*",
      (SCP_DESTINATION)   : "/path/to/destination",
      (SCP_EXECUTABLE)    : null,
      (SCP_HOST_KEY_CHECK): false
  ]

  @Before
  void setUp() throws Exception {
    super.setUp()
    underTest = new ScpCommandBuilderImpl(this.dslMock.getMock())
  }

  @Test
  void shouldBuildNonRecursiveWithoutUser() {
    underTest.applyConfig(configTemplate)
    assertEquals('scp -P 22 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null /path/to/source/* testhost:"/path/to/destination"', underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithRecursiveUserPortHostKeyCheck() {
    configTemplate[SCP_USER] = "testserveruser"
    configTemplate[SCP_RECURSIVE] = true
    configTemplate[SCP_HOST_KEY_CHECK] = true
    configTemplate[SCP_SOURCE] = "/path/with spaces/to/source dir/*"
    configTemplate[SCP_DESTINATION] = "/path/with spaces/to/destination folder"
    configTemplate[SCP_ARGUMENTS] = ["-arg1", "-arg2"]
    configTemplate[SCP_PORT] = 2222
    configTemplate[SCP_EXECUTABLE] = "/usr/sbin/scp"
    underTest.applyConfig(configTemplate)
    underTest.setCredential(new Credential("pattern", "id", "comment", "should-not-be-used-username"))
    assertEquals('/usr/sbin/scp -arg1 -arg2 -P 2222 -r /path/with\\ spaces/to/source\\ dir/* testserveruser@testhost:"/path/with\\ spaces/to/destination\\ folder"', underTest.build())
    assertEmptyAfterReset("/usr/sbin/scp")
  }

  @Test
  void shouldUseUserNameFromCredentialAndCustomExecutable() {
    underTest.commandBuilder.setExecutable("/usr/bin/scp")
    underTest.setHost("propertyhost")
    underTest.setSourcePath("source path/from property")
    underTest.setDestinationPath("destination path/from property")
    underTest.setCredential(new Credential("pattern", "id", "comment", "should-be-used-username"))
    assertEquals('/usr/bin/scp source\\ path/from\\ property should-be-used-username@propertyhost:"destination\\ path/from\\ property"', underTest.build())
    assertEmptyAfterReset("/usr/bin/scp")
  }

  @Test(expected = AbortException)
  void shouldFailWhenNoHostGiven() {
    configTemplate[SCP_HOST] = null
    underTest.applyConfig(configTemplate)
    underTest.build()
    assertEmptyAfterReset()
  }

  @Test(expected = AbortException)
  void shouldFailWhenNoSourceGiven() {
    configTemplate[SCP_SOURCE] = null
    underTest.applyConfig(configTemplate)
    underTest.build()
    assertEmptyAfterReset()
  }

  @Test(expected = AbortException)
  void shouldFailWhenNoDestinationGiven() {
    configTemplate[SCP_DESTINATION] = null
    underTest.applyConfig(configTemplate)
    underTest.build()
    assertEmptyAfterReset()
  }

  void assertEmptyAfterReset(String expectedExecutable = "scp") {
    underTest.reset()
    assertNull(underTest.getCredential())
    assertNull(underTest.getHost())
    assertNull(underTest.getUser())
    assertNull(underTest.getDestinationPath())
    assertNull(underTest.getSourcePath())
    assertEquals(expectedExecutable, underTest.getCommandBuilder().build())
  }

}
