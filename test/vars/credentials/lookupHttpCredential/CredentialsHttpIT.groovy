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
package vars.credentials.lookupHttpCredential

import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Assert
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

class CredentialsHttpIT extends LibraryIntegrationTestBase {

  @Test
  void shouldLookupHttpCredential() {
    Object stepResult = loadAndExecuteScript("vars/credentials/lookupHttpCredential/jobs/shouldLookupHttpCredentialTestJob.groovy")
    assertNotNull(stepResult)
    Credential foundCredential = (Credential) stepResult
    Assert.assertEquals("http-username-password-credential-for-server1",foundCredential.getId())
  }

  @Test
  void shouldNotLookupHttpCredential() {
    Object stepResult = loadAndExecuteScript("vars/credentials/lookupHttpCredential/jobs/shouldNotLookupHttpCredentialTestJob.groovy")
    assertNull(stepResult)
  }
}
