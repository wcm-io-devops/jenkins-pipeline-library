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
package io.wcm.devops.jenkins.pipeline.utils.resources

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import net.sf.json.JSONException
import net.sf.json.JSONObject
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class JsonLibraryResourceTest extends DSLTestBase {

  JsonLibraryResource underTest

  @Test
  void shouldLoadExistingJsonResource() {
    underTest = new JsonLibraryResource(this.dslMock.getMock(), 'example-resource.json')
    JSONObject actual = underTest.load()
    assertTrue(actual.containsKey('foo'))
    assertEquals("bar", actual.get('foo'))
  }

  @Test(expected = AbortException.class)
  void shouldFailOnNonExistingJsonResource() {
    underTest = new JsonLibraryResource(this.dslMock.getMock(), 'notexisting.json')
    underTest.load()
  }

  @Test(expected = JSONException.class)
  void shouldFailOnInvalidJson() {
    underTest = new JsonLibraryResource(this.dslMock.getMock(), 'invalid.json')
    underTest.load()
  }

}
