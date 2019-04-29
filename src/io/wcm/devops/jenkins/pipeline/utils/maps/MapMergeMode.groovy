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
