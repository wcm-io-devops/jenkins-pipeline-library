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
package io.wcm.devops.jenkins.pipeline.credentials

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import net.sf.json.JSON
import org.junit.Test

import static org.junit.Assert.assertEquals

class CredentialParserTest extends DSLTestBase {

  JsonLibraryResource jsonLibraryResource
  JSON testContent

  @Override
  void setUp() throws Exception {
    super.setUp()
    jsonLibraryResource = new JsonLibraryResource(this.dslMock.getMock(), "credentials/parser-test.json")
    testContent = jsonLibraryResource.load()
  }

  @Test
  void shouldOnlyReturnValidResources() {
    CredentialParser underTest = new CredentialParser()
    List<Credential> parseResult = underTest.parse(testContent)
    assertEquals("should only contain one managed file", 1, parseResult.size())
    Credential parsedCredential = parseResult.get(0)
    assertEquals("should-be-parsed-credential-pattern", parsedCredential.getPattern())
    assertEquals("should-be-parsed-credential-id", parsedCredential.getId())
    assertEquals("should-be-parsed-credential-comment", parsedCredential.getComment())
    assertEquals("should-be-parsed-credential-username", parsedCredential.getUserName())
  }

}
