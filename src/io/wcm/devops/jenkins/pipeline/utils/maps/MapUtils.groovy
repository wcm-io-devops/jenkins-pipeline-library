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
package io.wcm.devops.jenkins.pipeline.utils.maps

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Utility functions for Map objects
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
class MapUtils implements Serializable {

  private static final long serialVersionUID = 1L

  static Logger log = new Logger(this)

  static typeUtils = new TypeUtils()

  /**
   * Merges 0 to n Map objects recursively into one Map
   *
   * Overlapping keys will be overwritten by N+1 values.
   * E.g.
   *  map[0] has "key" with "value"
   *  map[1] has "key" with "newValue"
   *
   *  Resulting will have "key" with "newValue"
   *
   * @param maps 0 to n maps that have to me merged.
   * @return The merged map
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  static transient Map merge(Map... maps) {
    Map result

    if (maps.length == 0) {
      result = [:]
    } else if (maps.length == 1) {
      result = maps[0]
    } else {
      result = [:]
      maps.each { map ->
        map.each { k, v ->
          log.trace("result[k]: ", result[k])
          log.trace("v: ", v)
          /*log.trace("isList result[k]: ", TypeUtils.isList(result[k]))
          log.trace("isList v: ", TypeUtils.isList(v))*/
          if (result[k] != null && typeUtils.isMap(result[k])) {
            // unnecessary qualified reference is necessary here otherwise CPS / Sandbox will be violated
            result[k] = MapUtils.merge((Map) result[k], (Map) v)
          } else if (result[k] != null && typeUtils.isList(result[k]) && typeUtils.isList(v)) {
            // execute a list merge
            List list1 = (List) result[k]
            List list2 = (List) v

            for (Object list2Item : list2) {
              if (!list1.contains(list2Item))
                list1.add(list2Item)
            }
            result[k] = list1
          } else {
            result[k] = v
          }
        }
      }
    }

    result
  }


}
