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

import com.cloudbees.groovy.cps.NonCPS

/**
 * Utility functions for Lists because of missing white list functions
 */
class ListUtils implements Serializable {

  private static final long serialVersionUID = 1L

  /**
   * Workaround for blocked removeAt function
   *
   * @param list The list to be processed
   * @param idx The index to be removed
   * @return the manipulated list
   */
  @NonCPS
  public static List removeAt(List list, Integer idx) {
    Integer walkIdx = -1
    // walk through list and remove the item at the given index
    list.removeAll {
      walkIdx++
      if (idx == walkIdx) {
        return true
      }
      return false
    }
    return list
  }

  /**
   * Workaround for blocked indexOf function
   *
   * @param list The list to search in
   * @param item The item to search for
   * @return The index of the found item or -1 when item was not found
   */
  @NonCPS
  public static Integer indexOf(List list, Object item) {
    Integer foundPosition = -1
    for (int i = 0; i < list.size(); i++) {
      if (list[i] == item) {
        foundPosition = i
        break
      }
    }
    return foundPosition
  }

}
