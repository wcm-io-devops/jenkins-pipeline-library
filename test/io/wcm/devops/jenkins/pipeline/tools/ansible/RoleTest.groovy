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

package io.wcm.devops.jenkins.pipeline.tools.ansible

import org.junit.Test

import static org.junit.Assert.*

class RoleTest {

  @Test
  void shouldParseGalaxyRole() {
    Role underTest = new Role("some.rolename")
    assertEquals("some.rolename", underTest.getSrc())
    assertEquals("some.rolename", underTest.getName())
    assertNull(underTest.getScm())
    assertEquals("master", underTest.getVersion())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isGalaxyRole())
    assertFalse(underTest.isScmRole())
  }

  @Test
  void shouldBeAGitRoleWithoutVersion() {
    Role underTest = new Role("testSrc")
    underTest.setName("testName")
    underTest.setScm("git")

    assertEquals("testSrc", underTest.getSrc())
    assertEquals("testName", underTest.getName())
    assertEquals("git", underTest.getScm())
    assertEquals("master", underTest.getVersion())
    assertTrue(underTest.isValid())
    assertFalse(underTest.isGalaxyRole())
    assertTrue(underTest.isScmRole())
  }

  @Test
  void shouldBeAGitRoleWithVersion() {
    Role underTest = new Role("testSrc")
    underTest.setName("testName")
    underTest.setScm("git")
    underTest.setVersion("testVersion")

    assertEquals("testSrc", underTest.getSrc())
    assertEquals("testName", underTest.getName())
    assertEquals("git", underTest.getScm())
    assertEquals("testVersion", underTest.getVersion())
    assertTrue(underTest.isValid())
    assertFalse(underTest.isGalaxyRole())
    assertTrue(underTest.isScmRole())
  }

  @Test
  void shouldBeInvalidWhenSrcIsNull() {
    Role underTest = new Role(null)
    assertFalse(underTest.isValid())
  }

}
