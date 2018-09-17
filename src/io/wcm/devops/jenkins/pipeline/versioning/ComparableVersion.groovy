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
package io.wcm.devops.jenkins.pipeline.versioning

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Jenkins groovy sandbox compatible version of
 * https://github.com/apache/maven/blob/master/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ComparableVersion.java
 */
class ComparableVersion implements Comparable<ComparableVersion>, Serializable {

  public String value

  public ListItem items

  public String canonical

  private static final long serialVersionUID = 1L

  Logger log = new Logger(this)

  ComparableVersion(String version) {
    parseVersion(version)
  }

  @Override
  @NonCPS
  int compareTo(ComparableVersion comparableVersion) {
    return items.compareTo(comparableVersion.items)
  }

  @NonCPS
  void parseVersion(String version) {
    this.value = version

    items = new ListItem()
    version = version.toLowerCase()

    ListItem list = items

    List stack = []
    stack.push(list)

    boolean isDigit = false
    int startIndex = 0

    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i)

      if (c == '.') {
        if (i == startIndex) {
          list.add(IntegerItem.ZERO)
        } else {
          list.add(parseItem(isDigit, version.substring(startIndex, i)))
        }
        startIndex = i + 1
      } else if (c == '-') {
        if (i == startIndex) {
          list.add(IntegerItem.ZERO)
        } else {
          list.add(parseItem(isDigit, version.substring(startIndex, i)))
        }
        startIndex = i + 1

        list.add(list = new ListItem())
        stack.push(list)
      } else if (c =~ '^\\d$') {
        if (!isDigit && i > startIndex) {
          list.add(new StringItem(version.substring(startIndex, i), true))
          startIndex = i

          list.add(list = new ListItem())
          stack.push(list)
        }

        isDigit = true
      } else {
        if (isDigit && i > startIndex) {
          list.add(parseItem(true, version.substring(startIndex, i)))
          startIndex = i

          list.add(list = new ListItem())
          stack.push(list)
        }

        isDigit = false
      }
    }

    if (version.length() > startIndex) {
      list.add(parseItem(isDigit, version.substring(startIndex)))
    }

    for (Integer i = stack.size() - 1; i >= 0; i--) {
      list = (ListItem) stack[i]
      list.normalize()
    }

    canonical = items.toString()
  }

  @NonCPS
  Item parseItem(boolean isDigit, String buf) {
    return isDigit ? new IntegerItem(buf) : new StringItem(buf, false)
  }

  @Override
  @SuppressFBWarnings("EQ_UNUSUAL")
  @NonCPS
  boolean equals(Object o) {
    TypeUtils typeUtils = new TypeUtils()
    return (typeUtils.isComparableVersion(o)) && canonical.equals(((ComparableVersion) o).canonical)
  }

  @Override
  @NonCPS
  int hashCode() {
    return canonical.hashCode()
  }

  @Override
  @NonCPS
  String toString() {
    return value
  }

  @NonCPS
  String getCanonical() {
    return canonical
  }

}
