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
package io.wcm.tooling.jenkins.pipeline.utils

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFile
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFileConstants
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFileParser
import io.wcm.tooling.jenkins.pipeline.model.PatternMatchable
import io.wcm.tooling.jenkins.pipeline.utils.resources.JsonLibraryResource
import org.junit.Test

import static org.junit.Assert.*

class PatternMatcherMavenSettingsTest extends DSLTestBase {

  PatternMatcher underTest

  List<PatternMatchable> managedFiles

  @Override
  void setUp() throws Exception {
    super.setUp()
    underTest = new PatternMatcher()
    JsonLibraryResource res = new JsonLibraryResource(this.dslMock.getMock(), ManagedFileConstants.MAVEN_SETTINS_PATH)
    ManagedFileParser parser = new ManagedFileParser()
    managedFiles = parser.parse(res.load())
  }

  @Test
  void shouldFindSSHFormat() {
    ManagedFile settings = underTest.getBestMatch("git@subdomain.domain.tld:group1", this.managedFiles)
    assertNotNull("resulting managed file is null", settings)

    assertEquals("group1-maven-settings-id", settings.getId())
    assertEquals("group1-maven-settings-name", settings.getName())
    assertEquals("group1-maven-settings-comment", settings.getComment())
    assertEquals("subdomain.domain.tld[:/]group1", settings.getPattern())
  }

  @Test
  void shouldFindUrlFormat() {
    ManagedFile settings = underTest.getBestMatch("https://subdomain.domain.tld/group1", this.managedFiles)
    assertNotNull("resulting managed file is null", settings)
    assertEquals("group1-maven-settings-id", settings.getId())
    assertEquals("group1-maven-settings-name", settings.getName())
    assertEquals("group1-maven-settings-comment", settings.getComment())
    assertEquals("subdomain.domain.tld[:/]group1", settings.getPattern())
  }

  @Test
  void shouldFindBetterMatchWithSSHFormat() {
    ManagedFile settings = underTest.getBestMatch("git@subdomain.domain.tld:group1/project1", this.managedFiles)
    assertNotNull("resulting managed file is null", settings)
    assertEquals("group1-project1-maven-settings-id", settings.getId())
    assertEquals("group1-project1-maven-settings-name", settings.getName())
    assertEquals("group1-project1-maven-settings-comment", settings.getComment())
    assertEquals("subdomain.domain.tld[:/]group1/project1", settings.getPattern())
  }

  @Test
  void shouldFindBetterMatchWithURLFormat() {
    ManagedFile settings = underTest.getBestMatch("https://subdomain.domain.tld/group1/project2", this.managedFiles)
    assertNotNull("resulting managed file is null", settings)
    assertEquals("group1-project2-maven-settings-id", settings.getId(),)
    assertEquals("group1-project2-maven-settings-name", settings.getName(),)
    assertEquals("group1-project2-maven-settings-comment", settings.getComment(),)
    assertEquals("subdomain.domain.tld[:/]group1/project2", settings.getPattern())
  }

  @Test
  void shouldFindNothing() {
    ManagedFile settings = underTest.getBestMatch("should-not-find-me", this.managedFiles)
    assertNull("There should be no found file", settings)
  }
}
