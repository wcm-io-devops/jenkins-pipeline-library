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

import com.cloudbees.groovy.cps.NonCPS
import io.wcm.tooling.jenkins.pipeline.utils.ListUtils

/**
 * Jenkins groovy sandbox compatible version of
 * https://github.com/apache/maven/blob/master/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ComparableVersion.java / StringItem
 */
class StringItem implements Item, Serializable {

  static final long serialVersionUID = 1L

  public static final List<String> _QUALIFIERS = ["alpha", "beta", "milestone", "rc", "snapshot", "", "sp"];

  /**
   * A comparable value for the empty-string qualifier. This one is used to determine if a given qualifier makes
   * the version older than one without a qualifier, or more recent.
   */
  private static final String RELEASE_VERSION_INDEX = String.valueOf(_QUALIFIERS.indexOf(""))

  String value = null

  Map ALIASES = [
      "ga"   : "",
      "final": "",
      "cr"   : "rc"
  ]

  StringItem(String value, boolean followedByDigit) {
    if (followedByDigit && value.length() == 1) {
      // a1 = alpha-1, b1 = beta-1, m1 = milestone-1
      switch (value.charAt(0)) {
        case 'a':
          value = "alpha"
          break
        case 'b':
          value = "beta"
          break
        case 'm':
          value = "milestone"
          break
        default:
          break
      }
    }
    if (ALIASES[value] != null) {
      this.value = ALIASES[value]
    } else {
      this.value = value
    }
  }

  @Override
  @NonCPS
  int compareTo(Item item) {
    if (item == null) {
      // 1-rc < 1, 1-ga > 1
      return comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX)
    }
    switch (item.getType()) {
      case INTEGER_ITEM:
        return -1; // 1.any < 1.1 ?

      case STRING_ITEM:
        return comparableQualifier(value).compareTo(comparableQualifier(((StringItem) item).value))

      case LIST_ITEM:
        return -1; // 1.any < 1-1

      default:
        throw new RuntimeException("invalid item: " + item.getClass())
    }
  }

  @Override
  @NonCPS
  int getType() {
    return STRING_ITEM
  }

  @Override
  @NonCPS
  boolean isNull() {
    return (comparableQualifier(value).compareTo(RELEASE_VERSION_INDEX) == 0)
  }

  /**
   * Returns a comparable value for a qualifier.
   *
   * This method takes into account the ordering of known qualifiers then unknown qualifiers with lexical
   * ordering.
   *
   * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1
   * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,
   * so this is still fast. If more characters are needed then it requires a lexical sort anyway.
   *
   * @param qualifier
   * @return an equivalent value that can be used with lexical comparison
   */
  @NonCPS
  String comparableQualifier(String qualifier) {
    int i = ListUtils.indexOf(_QUALIFIERS, qualifier)
    return i == -1 ? (_QUALIFIERS.size() + "-" + qualifier) : "$i"
  }

  @Override
  @NonCPS
  String toString() {
    return value
  }
}
