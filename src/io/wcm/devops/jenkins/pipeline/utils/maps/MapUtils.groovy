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
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.MAP_MERGE_MODE

/**
 * Utility functions for Map objects
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
class MapUtils implements Serializable {

  private static final long serialVersionUID = 1L

  static Logger log = new Logger(this)

  static typeUtils = new TypeUtils()

  static MapUtils instance

  /**
   * Merges 0 to n Map objects recursively into one Map
   *
   * Overlapping keys will handled by the defined merge mode.
   * Therefore a key with ConfigConstants.MAP_MERGE_MODE has to be present
   * in one of the maps at the needed level.
   *
   * Per default the merge mode is "Merge" which will result in
   * - merged arrays (unique)
   * - merged maps
   * - value overwriting
   *
   * E.g.
   *  map[0] has "key" with "value"
   *  map[1] has "key" with "newValue"
   *
   * Resulting will have "key" with "newValue"
   *
   * When MapMergeMode is REPLACE, overlapping keys from the n+1 map will overwrite
   * keys from n map
   *
   * When MapMergeMode is SKIP, overlapping keys from n will not be overwritten by
   * keys from n+1 map
   *
   * @param maps 0 to n maps that have to me merged.
   * @return The merged map
   */
  @NonCPS
  @SuppressFBWarnings('SE_NO_SERIALVERSIONID')
  static transient Map merge(Map... maps) {
    Map result
    instance = new MapUtils()
    if (maps == null || maps.length == 0) {
      result = [:]
    } else if (maps.length == 1) {
      result = maps[0]
    } else {
      result = [:]
      maps.each { map ->
        // get the merge mode from current result
        MapMergeMode mergeMode = (MapMergeMode) result[MAP_MERGE_MODE] ?: null
        // get the merge mode from incoming map when not in result
        mergeMode = mergeMode ?: (MapMergeMode) map[MAP_MERGE_MODE] ?: null
        // set default merge mode when absolutely nothing was found
        mergeMode = mergeMode ?: MapMergeMode.MERGE
        // walk through map
        map.each { k, v ->
          try {
            v = v.clone()
          } catch (Exception ex) {
            // do nothing here
          }

          if (result[k] != null && typeUtils.isMap(result[k]) && typeUtils.isMap(v)) {
            // unnecessary qualified reference is necessary here otherwise CPS / Sandbox will be violated
            result[k] = instance.mergeMap((Map) result[k], (Map) v, mergeMode)
          } else if (result[k] != null && typeUtils.isList(result[k]) && typeUtils.isList(v)) {
            // execute a list merge
            List existingList = (List) result[k]
            List incomingList = (List) v
            result[k] = instance.mergeList(existingList, incomingList, mergeMode)
          } else if (result[k] != null && v != null) {
            result[k] = instance.mergeValue(result[k], v, mergeMode)
          }
          else {
            result[k] = v
          }
        }
      }
    }

    result
  }

  /**
   * Merges two maps based on the provided merge mode
   * - MapMergeMode.SKIP keeps the existing map untouched and returns it
   * - MapMergeMode.REPLACE = returns the incoming map since it should replace everything
   * - MapMergeMode.MERGE = will merge the two maps by calling another recursion level
   *
   * @param existing Existing map object
   * @param incoming Incoming map object
   * @param mergeMode The merge mode which should apply
   * @return the resulting map based on the merge mode
   *
   * @see MapMergeMode
   */
  @NonCPS
  Map mergeMap(Map existing, Map incoming, MapMergeMode mergeMode) {
    switch (mergeMode) {
      case MapMergeMode.SKIP: return existing
      case MapMergeMode.REPLACE: return incoming
      // merge all other options
      default:
        return MapUtils.merge(existing, incoming)
        break
    }
  }

  /**
   * Merge function for simple objects like string, boolean etc.
   * - MapMergeMode.MERGE and REPLACE will replace the existing value by the incoming value
   * - MapMergeMode.SKIP will keep the existing value
   *
   * @param existing The existing value
   * @param incoming The incoming value
   * @param mergeMode The merge mode which should apply
   * @return The resulting value based on the merge mode
   *
   * @see MapMergeMode
   */
  @NonCPS
  Object mergeValue(Object existing, Object incoming, MapMergeMode mergeMode) {
    // merge mode object are protected and can not be overwritten
    if (typeUtils.isMapMergeMode(existing)) {
      return existing
    }
    switch (mergeMode) {
      case MapMergeMode.SKIP: return existing
    // all other options are using the incoming value
      default:
        return incoming
        break
    }
  }

  /**
   * Merge function for list objects
   * - MapMergeMode.SKIP keeps the existing list untouched and returns it
   * - MapMergeMode.REPLACE = returns the incoming list
   * - MapMergeMode.MERGE = will merge the two lists
   *
   * @param existing The existing list
   * @param incoming The incoming list
   * @param mergeMode The merge mode which should apply
   * @return The resulting list based on the merge mode
   *
   * @see MapMergeMode
   */
  @NonCPS
  List mergeList(List existing, List incoming, MapMergeMode mergeMode) {
    switch (mergeMode) {
      case MapMergeMode.SKIP: return existing
      case MapMergeMode.REPLACE: return incoming
      case MapMergeMode.MERGE:
        for (Object incomingListItem : incoming) {
          if (!existing.contains(incomingListItem))
            existing.add(incomingListItem)
        }
        return existing
      default:
        return existing
        break
    }
  }

}
