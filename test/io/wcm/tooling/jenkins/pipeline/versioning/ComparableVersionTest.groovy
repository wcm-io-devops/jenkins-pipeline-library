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
package io.wcm.tooling.jenkins.pipeline.versioning

import org.junit.Test

import static org.junit.Assert.*

class ComparableVersionTest {

  private static final List<String> VERSIONS_QUALIFIER =
      ["1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2",
       "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot",
       "1-1", "1-2", "1-123"]

  private static final List<String> VERSIONS_NUMBER =
      ["2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1",
       "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m"]


  private void checkVersionsOrder(List<String> versions) {
    Comparable[] c = new Comparable[versions.size()]
    for (int i = 0; i < versions.size(); i++) {
      c[i] = newComparable(versions[i])
    }

    for (int i = 1; i < versions.size(); i++) {
      Comparable low = c[i - 1]
      for (int j = i; j < versions.size(); j++) {
        Comparable high = c[j]
        assertTrue("expected " + low + " < " + high, low.compareTo(high) < 0)
        assertTrue("expected " + high + " > " + low, high.compareTo(low) > 0)
      }
    }
  }

  @Test
  void testVersionsQualifier() {
    checkVersionsOrder(VERSIONS_QUALIFIER)
  }

  @Test
  void testVersionsNumber() {
    checkVersionsOrder(VERSIONS_NUMBER)
  }

  @Test
  void shouldCompareVersionsInOrder() {
    assertVersionsOrder("1", "2")
    assertVersionsOrder("1.5", "2")
    assertVersionsOrder("1", "2.5")
    assertVersionsOrder("1.0", "1.1")
    assertVersionsOrder("1.1", "1.2")
    assertVersionsOrder("1.0.0", "1.1")
    assertVersionsOrder("1.0.1", "1.1")
    assertVersionsOrder("1.1", "1.2.0")

    assertVersionsOrder("1.0-alpha-1", "1.0")
    assertVersionsOrder("1.0-alpha-1", "1.0-alpha-2")
    assertVersionsOrder("1.0-alpha-1", "1.0-beta-1")

    assertVersionsOrder("1.0-beta-1", "1.0-SNAPSHOT")
    assertVersionsOrder("1.0-SNAPSHOT", "1.0")
    assertVersionsOrder("1.0-alpha-1-SNAPSHOT", "1.0-alpha-1")

    assertVersionsOrder("1.0", "1.0-1")
    assertVersionsOrder("1.0-1", "1.0-2")
    assertVersionsOrder("1.0.0", "1.0-1")

    assertVersionsOrder("2.0-1", "2.0.1")
    assertVersionsOrder("2.0.1-klm", "2.0.1-lmn")
    assertVersionsOrder("2.0.1", "2.0.1-xyz")

    assertVersionsOrder("2.0.1", "2.0.1-123")
    assertVersionsOrder("2.0.1-xyz", "2.0.1-123")
  }

  @Test
  void shouldReturnCorrectCanonicalizedVersion() {
    ComparableVersion v1 = new ComparableVersion("1.2.3")
    assertEquals("1.2.3", v1.getCanonical())
  }

  @Test
  void versionsShouldBeEqual() {
    newComparable("1.0-alpha");
    assertEqualVersion("1", "1")
    assertEqualVersion("1", "1.0")
    assertEqualVersion("1", "1.0.0")
    assertEqualVersion("1.0", "1.0.0")
    assertEqualVersion("1", "1-0")
    assertEqualVersion("1", "1.0-0")
    assertEqualVersion("1.0", "1.0-0")

    assertEqualVersion("1a", "1-a")
    assertEqualVersion("1a", "1.0-a")
    assertEqualVersion("1a", "1.0.0-a")
    assertEqualVersion("1.0a", "1-a")
    assertEqualVersion("1.0.0a", "1-a")
    assertEqualVersion("1x", "1-x")
    assertEqualVersion("1x", "1.0-x")
    assertEqualVersion("1x", "1.0.0-x")
    assertEqualVersion("1.0x", "1-x")
    assertEqualVersion("1.0.0x", "1-x")

    // aliases
    assertEqualVersion("1ga", "1")
    assertEqualVersion("1final", "1")
    assertEqualVersion("1cr", "1rc")

    // special "aliases" a, b and m for alpha, beta and milestone
    assertEqualVersion("1a1", "1-alpha-1")
    assertEqualVersion("1b2", "1-beta-2")
    assertEqualVersion("1m3", "1-milestone-3")

    // case insensitive
    assertEqualVersion("1X", "1x")
    assertEqualVersion("1A", "1a")
    assertEqualVersion("1B", "1b")
    assertEqualVersion("1M", "1m")
    assertEqualVersion("1Ga", "1")
    assertEqualVersion("1GA", "1")
    assertEqualVersion("1Final", "1")
    assertEqualVersion("1FinaL", "1")
    assertEqualVersion("1FINAL", "1")
    assertEqualVersion("1Cr", "1Rc")
    assertEqualVersion("1cR", "1rC")
    assertEqualVersion("1m3", "1Milestone3")
    assertEqualVersion("1m3", "1MileStone3")
    assertEqualVersion("1m3", "1MILESTONE3")
  }

  void assertEqualVersion(String v1, String v2) {
    Comparable c1 = newComparable(v1)
    Comparable c2 = newComparable(v2)
    assertTrue("expected " + v1 + " == " + v2, c1.compareTo(c2) == 0)
    assertTrue("expected " + v2 + " == " + v1, c2.compareTo(c1) == 0)
    assertTrue("expected same hashcode for " + v1 + " and " + v2, c1.hashCode() == c2.hashCode())
    assertTrue("expected " + v1 + ".equals( " + v2 + " )", c1.equals(c2))
    assertTrue("expected " + v2 + ".equals( " + v1 + " )", c2.equals(c1))
  }

  void assertVersionsOrder(String v1, String v2) {
    Comparable c1 = newComparable(v1)
    Comparable c2 = newComparable(v2)
    assertTrue("expected " + v1 + " < " + v2, c1.compareTo(c2) < 0)
    assertTrue("expected " + v2 + " > " + v1, c2.compareTo(c1) > 0)
  }

  void assertGreaterThan(ComparableVersion v1, ComparableVersion v2) {
    assertTrue("'$v1' should be greater than '$v2'", v1 > v2)
  }

  void assertNotGreaterThan(ComparableVersion v1, ComparableVersion v2) {
    assertFalse("'$v1' should not be greater than than '$v2'", v1 > v2)
  }

  /**
   * Test <a href="https://issues.apache.org/jira/browse/MNG-5568">MNG-5568</a> edge case
   * which was showing transitive inconsistency: since A &gt; B and B &gt; C then we should have A &gt; C
   * otherwise sorting a list of ComparableVersions() will in some cases throw runtime exception;
   * see Netbeans issues <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=240845">240845</a> and
   * <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=226100">226100</a>
   */
  @Test
  void testMng5568() {
    String a = "6.1.0";
    String b = "6.1.0rc3";
    String c = "6.1H.5-beta"; // this is the unusual version string, with 'H' in the middle

    assertVersionsOrder(b, a); // classical
    assertVersionsOrder(b, c); // now b < c, but before MNG-5568, we had b > c
    assertVersionsOrder(a, c);
  }

  @Test
  void testReuse() {
    ComparableVersion c1 = new ComparableVersion("1");
    c1.parseVersion("2");

    Comparable c2 = newComparable("2");

    assertEquals("reused instance should be equivalent to new instance", c1, c2);
  }

  Comparable newComparable(String version) {
    ComparableVersion ret = new ComparableVersion(version)
    String canonical = ret.getCanonical()
    String parsedCanonical = new ComparableVersion(canonical).getCanonical()

    System.out.println("canonical( " + version + " ) = " + canonical)
    assertEquals("canonical( " + version + " ) = " + canonical + " -> canonical: " + parsedCanonical, canonical,
        parsedCanonical)

    return ret
  }
}
