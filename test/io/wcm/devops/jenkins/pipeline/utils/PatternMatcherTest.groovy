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
package io.wcm.devops.jenkins.pipeline.utils

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.model.PatternMatchable
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class PatternMatcherTest extends DSLTestBase {

  PatternMatcher underTest

  @Override
  @Before
  void setUp() {
    super.setUp()
    underTest = new PatternMatcher()
  }

  @Test
  void shouldFindMatch() {
    PatternMatchable result = underTest.getBestMatch("pattern1", this.createTestCredentials())
    assertNotNull("The shouldFindMatch should find one Credential", result)
    assertEquals("pattern1-id", result.getId())
  }

  @Test
  void shouldFindFirstMatch() {
    PatternMatchable result = underTest.getBestMatch("pattern", this.createTestCredentials())
    assertNotNull("The shouldFindFirstMatch should find one Credential", result)
    assertEquals("pattern-id", result.getId())
  }

  @Test
  void shouldFindBetterMatch() {
    PatternMatchable result = underTest.getBestMatch("pattern-better", this.createTestCredentials())
    assertNotNull("The shouldFindBetterMatch should find one Credential", result)
    assertEquals("i-am-a-better-match-id", result.getId())
  }

  @Test
  void shouldMatchCorrectTldDomainCredentials() {
    PatternMatchable result = underTest.getBestMatch("www.domain.tld", this.createTestCredentials())
    assertNotNull("The shouldMatchCorrectDomainCredentials should find one Credential", result)
    assertEquals("domain-matched", result.getId())
  }

  @Test
  void shouldMatchCorrectSubDomainCredentials() {
    PatternMatchable result = underTest.getBestMatch("sub3.sub2.sub1.domain.tld", this.createTestCredentials())
    assertNotNull("The shouldMatchCorrectDomainCredentials should find one Credential", result)
    assertEquals("detailed-domain-matched", result.getId())
  }

  @Test
  void shouldMatchWithCaptureGroup() {
    List<Credential> files = new ArrayList<Credential>()
    files.push(new Credential("mandatory-part(-optional-part)?.domain.tld", "pattern1-id", "pattern1-name"))
    PatternMatchable actualResult1 = underTest.getBestMatch("mandatory-part.domain.tld", files)
    PatternMatchable actualResult2 = underTest.getBestMatch("mandatory-part-optional-part.domain.tld", files)

    assertNotNull(actualResult1)
    assertNotNull(actualResult2)

    assertEquals("pattern1-id", actualResult1.getId())
    assertEquals("pattern1-id", actualResult2.getId())
  }

  List<Credential> createTestCredentials() {
    List<Credential> files = new ArrayList<Credential>()
    files.push(new Credential("pattern1", "pattern1-id", "pattern1-name"))
    files.push(new Credential("pattern2", "pattern2-id", "pattern2-name"))
    files.push(new Credential("pattern", "pattern-id", "pattern2-name"))
    files.push(new Credential("pattern", "i-should-not-be-returned-id", "i-should-not-be-returned-name"))
    files.push(new Credential("pattern-b", "i-am-a-better-match-id", "i-am-a-better-match-name"))
    files.push(new Credential(/.*\.domain\.tld/, "domain-matched", "domain-matched-name"))
    files.push(new Credential(/.*\.sub2\.sub1\.domain\.tld/, "detailed-domain-matched", "detailed-domain-matched-name"))


    return files
  }

}
