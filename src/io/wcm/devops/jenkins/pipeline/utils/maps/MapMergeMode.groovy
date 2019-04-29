/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2019 wcm.io DevOps
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
/**
 * Enumeration class for map merge modes
 *
 * MapMergeMode.MERGE
 * - merges lists into a unique list ( [1,2] + [2,3] = [1,2,3]
 * - merges maps into a map with value overriding ([p1:v1, p2:v2] + [p3:v3, p2:v2changed] = [p1:v1, p2:v2changed, p3:v3])
 * - merges values by overwriting the existing with the incoming
 *
 * MapMergeMode.SKIP
 * - keeps all overlapping keys in the existing map without modifying them
 *
 * MapMergeMode.REPLACE
 * - replaces all overlapping keys in the existing map with the incoming values
 */
enum MapMergeMode implements Serializable {

  MERGE,
  SKIP,
  REPLACE,
}
