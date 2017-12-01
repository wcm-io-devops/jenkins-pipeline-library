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
package io.wcm.devops.jenkins.pipeline.shell

import org.junit.Test

import static org.junit.Assert.assertEquals

class ShellUtilsTest {

  @Test
  void shouldEscapeSpaces() {
    String actual = ShellUtils.escapePath("folder with spaces/subfolder with spaces/filename with spaces.txt")
    assertEquals('folder\\ with\\ spaces/subfolder\\ with\\ spaces/filename\\ with\\ spaces.txt', actual)
  }

  @Test
  void shouldRemoveSingleQuotes() {
    String actual = ShellUtils.escapePath("'folder with spaces/subfolder with spaces'")
    assertEquals("folder\\ with\\ spaces/subfolder\\ with\\ spaces", actual)
  }

  @Test
  void shouldRemoveDoubleQuotes() {
    String actual = ShellUtils.escapePath('"folder with spaces/subfolder with spaces"')
    assertEquals("folder\\ with\\ spaces/subfolder\\ with\\ spaces", actual)
  }

  @Test
  void shouldReturnNull() {
    String actual = ShellUtils.escapePath(null)
    assertEquals(null, actual)
  }

  @Test
  void shouldOnlyRemoveSurroundingDoubleQuotes() {
    assertEquals('va\\"lue', ShellUtils.escapePath('va"lue"'))
    assertEquals('va\\"lue', ShellUtils.escapePath('"va"lue'))
  }

  @Test
  void shouldOnlyRemoveSurroundingSingleQuotes() {
    assertEquals("va\\\'lue", ShellUtils.escapePath("va'lue'"))
    assertEquals("va\\\'lue", ShellUtils.escapePath("'va'lue"))
  }

  @Test
  void shouldOnlyTrimBeginningAndEndingDoubleQuote() {
    assertEquals('"val"ue"', ShellUtils.trimDoubleQuote('""val"ue""'))
  }

  @Test
  void shouldOnlyTrimBeginningAndEndingSingleQuote() {
    assertEquals("'val'ue'", ShellUtils.trimSingleQuote("''val'ue''"))
  }

  @Test
  void shouldEscapeShellCharacters() {
    assertEquals("\\\\\\ \\\'\\\"\\!\\#\\\$\\&\\(\\)\\,\\;\\<\\>\\?\\[\\]\\^\\`\\{\\|\\}", ShellUtils.escapeShellCharacters("\\ '\"!#\$&(),;<>?[]^`{|}"))
  }

}
