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
package io.wcm.tooling.jenkins.pipeline.managedfiles

import org.junit.Test

import static org.junit.Assert.*

class MangedFileTest {

  @Test
  void shouldReturnConstructorValues() {
    ManagedFile underTest = new ManagedFile("test-pattern", "test-id", "test-name", "test-comment")
    assertEquals("test-pattern", underTest.getPattern())
    assertEquals("test-id", underTest.getId())
    assertEquals("test-name", underTest.getName())
    assertEquals("test-comment", underTest.getComment())
  }

  @Test
  void shouldBeInvalidWhenPatternIsNull() {
    ManagedFile underTest = new ManagedFile(null, "test-id")
    assertFalse(underTest.isValid())
  }

  @Test
  void shouldBeInvalidWhenIdIsNull() {
    ManagedFile underTest = new ManagedFile("test-pattern", null)
    assertFalse(underTest.isValid())
  }

  @Test
  void shouldBeInvalidWhenPatternAndIdIsNull() {
    ManagedFile underTest = new ManagedFile(null, null)
    assertFalse(underTest.isValid())
  }

  @Test
  void shouldBeValidWhenPatternAndIdIsSet() {
    ManagedFile underTest = new ManagedFile("valid-pattern", "valid-id")
    assertTrue(underTest.isValid())
    assertEquals("valid-pattern", underTest.getPattern())
    assertEquals("valid-id", underTest.getId())
  }

}
