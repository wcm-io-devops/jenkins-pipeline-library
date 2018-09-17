/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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

/**
 * Jenkins groovy sandbox compatible version of
 * https://github.com/apache/maven/blob/master/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ComparableVersion.java / ListItem
 */
class ListItem extends ArrayList<Item> implements Item {

  static final long serialVersionUID = 1L

  @Override
  @NonCPS
  int compareTo(Item item) {
    if (item == null) {
      if (size() == 0) {
        return 0 // 1-0 = 1- (normalize) = 1
      }
      Item first = get(0);
      return first.compareTo(null)
    }
    switch (item.getType()) {
      case INTEGER_ITEM:
        return -1 // 1-1 < 1.0.x

      case STRING_ITEM:
        return 1 // 1-1 > 1-sp

      case LIST_ITEM:
        Iterator<Item> left = iterator()
        Iterator<Item> right = ((ListItem) item).iterator()

        while (left.hasNext() || right.hasNext()) {
          Item l = left.hasNext() ? left.next() : null
          Item r = right.hasNext() ? right.next() : null

          // if this is shorter, then invert the compare and mul with -1
          int result = l == null ? (r == null ? 0 : -1 * r.compareTo(l)) : l.compareTo(r)

          if (result != 0) {
            return result
          }
        }

        return 0

      default:
        throw new RuntimeException("invalid item: " + item.getClass())
    }
  }

  @Override
  @NonCPS
  int getType() {
    return LIST_ITEM
  }

  @Override
  @NonCPS
  boolean isNull() {
    return (size() == 0)
  }

  @NonCPS
  void normalize() {
    for (int i = size() - 1; i >= 0; i--) {
      Item lastItem = get(i)

      if (lastItem.isNull()) {
        // remove null trailing items: 0, "", empty list
        remove(i)
      } else if (!(isListItem(lastItem))) {
        break
      }
    }
  }

  @NonCPS
  String toString() {
    String result = ""
    for (Item item : this) {
      if (result.length() > 0) {
        if (isListItem(item)) {
          result = "${result}-"
        } else {
          result = "${result}."
        }
      }
      result = "$result${item.toString()}"
    }
    return result
  }

  /**
   * Utility function to return true for all ListItem objects
   *
   * @param object ListItem object
   * @return true
   */
  @NonCPS
  Boolean isListItem(ListItem object) {
    return true
  }

  /**
   * Utility function to return false for all non ListItem objects
   *
   * @param object Any other object that is not of type ListItem
   * @return false
   */
  @NonCPS
  Boolean isListItem(Object object) {
    return false
  }
}
