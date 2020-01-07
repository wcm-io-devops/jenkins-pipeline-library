# Configuration map merging

The jenkins-pipeline-library contains the helper
[`MapUtils.groovy`](../src/io/wcm/devops/jenkins/pipeline/utils/maps/MapUtils.groovy)
which is heavily used to integrate custom changes into the configuration
map.

`MapUtils.groovy` will merge 2-n Maps recursively together and returns
the merged result without modifying the original maps.

# Table of contents
* [Merge rules](#merge-rules)
  * [Add](#add)
  * [Overwrite](#overwrite)
  * [Merge](#merge)
    * [Merging Lists](#merging-lists)
    * [Merging Maps](#merging-maps)
  * [Merge order](#merge-order)
* [MapMergeMode](#merge-modes)
  * [`MapMergeMode.MERGE`](#mapmergemodemerge)
  * [`MapMergeMode.SKIP`](#mapmergemodeskip)
  * [`MapMergeMode.REPLACE`](#mapmergemodereplace)
  * [MapMergeMode scope](#mapmergemode-scope)

## Merge rules

The MapUtils tool merges maps using the following rules.

### Add

Keys that are unique in each map to merge are just "merged" together.

```groovy
map1 = ["property_map1": "value_map1"]
map2 = ["property_map2": "value_map2"]
result1 = MapUtils.merge(map1, map2)

/*
result1 = [
  "property_map1" : "value_map1",
  "property_map2" : "value_map2"
]
 */
```

* `property_map2` did not exist in `map1` so it was just added.

### Overwrite

When items in the map can not be merged they are overwritten by the map
"to the right".

```groovy
map1 = ["int": 1, "string": "1", "list": [1], "map": ["key": "map1Value"]]
map2 = ["int": 2, "string": "2", "list": 2, "map": 2]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)

/*
result1 = [ "int" : 2, "string" : "2", "list" : 2, "map" : 2 ]
result2 = [ "int" : 1, "string" : "1", "list" : [1], "map" : [ "key" : "map1Value" ] ]
*/
```

* `result1`: When `map2` was merged into `map1` all keys were present in
  both maps, but the datatypes were not mergeable so the items from
  `map2` replaced the items from `map1`
* `result2`: This is the opposite to `result1` but here `map1` was
  merged into `map2` so the values from `map1` replaced the items from
  `map2`

### Merge

The following datatypes support merging:
* `java.util.List`
* `java.util.Map`

So when the datatype is one of the above ones and the datatype is equal
for an item key the item is merged.

#### Merging Lists

```groovy
map1 = ["list": [1, 0]]
map2 = ["list": [2, 0]]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)

/*
result1 = [ "list" : [ 1, 0, 2 ] ]
result2 = [ "list" : [ 2, 0, 1 ] ]
*/
```

As you can see both results contain the same elements in different
order, depending if you merge `map2` into `map1` or the other way
around.

#### Merging Maps

Maps will be merged recursively following the rules from above for
Adding, Overwriting and Merging items.

```groovy
map1 = [lvl1: [map1Item: "map1Value", list: [1], int: 1, str: "1", "lvl2": [map1: "1"]]]
map2 = [lvl1: [map2Item: "map2Value", list: [2], int: 2, str: "2", "lvl2": [map2: "2"]]]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)
/*
result1 = [lvl1:[map1Item: "map1Value", list: [1, 2], int: 2, str: "2", lvl2 : [map1: "1", map2: "2"], map2Item: "map2Value" ]]
result2 = [lvl1:[map2Item: "map2Value", list: [2, 1], int: 1, str: "1", lvl2 : [map2: "2", map1: "1"], map1Item: "map1Value" ]]
 */
```

##### `result1` explanation

* The `lvl1` item from `map2` was integrated into the `lvl1` item from
  `map1`
* The `list` from `map1` and `map2` was merged because the items from
  both maps were of the type `java.util.List`
* The items `int` and `str` from `map2` replaced the corresponding items
  from `map1`
* The map item `lvl2` from `map2` merged with the corresponding item
  from  `map1`
* The item `map2Item` was added

##### `result2` explanation

The `result2` is like the `result1` but items from `map1` had precedence
before items from `map2`.

### Merge order

The passed maps will be processed from the left to the right which means
that the second Map will overwrite the first Map.

```groovy
Map map1 = [ "source" : "map1", "str1" : "hello map1" ] 
Map map2 = [ "source" : "map2", "str2" : "hello map2" ] 
Map map3 = [ "source" : "map3", "str3" : "hello map3" ] 

Map result1 = MapUtils.merge(map1, map2) 
Map result2 = MapUtils.merge(map1, map2, map3) 
/*
result1 = [ 
            "source" : "map1", 
            "map1" : "hello map1",
            "map2" : "hello map2" 
          ]
result2 = [ 
            "source" : "map1", 
            "map1" : "hello map1",
            "map2" : "hello map2",
            "map2" : "hello map3" 
          ]
*/
```

For **result1** the merge was applied as follows
* `map1` and `map2` both contained `source` and `source` is a simple
  data type (`string`) so the value from `map2` replaced the value of
  `map1`
* `str2` from `map2` was simply added to the result because is did not
  exist in `map1`

For **result2** the merge was applied as follows
* `map1` and `map2` both contained `source` and `source` is a simple
  data type (`string`) so the value from `map2` replaced the value of
  `map1`
* `str2` from `map2` was simply added to the result because is did not
  exist in `map1`
*  `map3` also contained `source` and `source` is a simple data type
   (`string`) so the value from the merge of `map1` and `map2` were
   replaced by the value of `map3`
* `str3` from `map3` was simply added to the result because is did not
  exist in `map1` nor `map2`

## Merge Modes

With version 1.6.0 of the jenkins pipeline library merge modes were
introduced to provide more control about the merge behavior.

The existing, default merge mode is `MapMergeMode.MERGE`.
The corresponding config constant is `ConfigConstants.MAP_MERGE_MODE`.

The used map merge mode is retrieved in the following order:
* use the `MapMergeMode` from the existing / "left" map, if defined
* use the `MapMergeMode` from the incoming / "right" map, if defined
* use the `MapMergeMode.MERGE` as default

**Example definition:**
```groovy
import io.wcm.devops.jenkins.pipeline.utils.ConfigConstants
import io.wcm.devops.jenkins.pipeline.utils.maps.MapMergeMode 

Map config = [
    (ConfigConstants.MAP_MERGE_MODE) : MapMergeMode.MERGE
  ]
```

### `MapMergeMode.MERGE`

This is the default merge mode and follows the 
[merge rules](#merge-rules).

### `MapMergeMode.SKIP`

SKIP the merge, keep the existing value, do
not overwrite items in the "left" map with items in the "right" map.

```groovy
map1 = [int: 1, string: "1", list: [1], map: [map1Key: "map1Value"], map1Item: "map1Item", (MAP_MERGE_MODE): MapMergeMode.SKIP]
map2 = [int: 2, string: "2", list: [2], map: [map2Key: "map2Value"], map2Item: "map2Item" ]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)
/*
result1 = [int: 1, string: "1", list: [1], map: [map1Key: "map1Value"], map1Item: "map1Item", mapMergeMode: "SKIP", map2Item: "map2Item"]
result2 = [int: 2, string: "2", list: [2], map: [map2Key: "map2Value"], map2Item: "map2Item", map1Item: "map1Item", mapMergeMode: "SKIP"]
 */
```

For **result1** the merge was applied as follows:
* each value for a key present in `map1` and `map2` was taken from
  `map1` - so the `map` and the `list` item were not merged!
* All other "non-overlapping" items were added (e.g. `map2Item`)

For **result2** the merge was applied as follows:
* each value for a key present in `map1` and `map2` was taken from
  `map2` - so the `map` and the `list` item were not merged!
* All other "non-overlapping" items were added (e.g. `map1Item` and `mapMergeMode`)

### `MapMergeMode.REPLACE`

This mode will replace all existing / overlapping keys in the map to the
left with the items from the map to the right. So this mode is the
opposite of `MapMergeMode.SKIP`.

```groovy
map1 = [int: 1, string: "1", list: [1], map: [map1Key: "map1Value"], map1Item: "map1Item", (MAP_MERGE_MODE): MapMergeMode.REPLACE]
map2 = [int: 2, string: "2", list: [2], map: [map2Key: "map2Value"], map2Item: "map2Item"]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)
/*
result1 = [int: 2, string: "2", list: [2], map: [ map2Key: "map2Value"], map1Item: "map1Item", mapMergeMode: "REPLACE", map2Item: "map2Item"]
result2 = [int: 1, string: "1", list: [1], map: [ map1Key: "map1Value"], map2Item: "map2Item", map1Item: "map1Item" , mapMergeMode: "REPLACE"]
*/
```

For **result1** the merge was applied as follows:
* each value for a key present in `map1` and `map2` was taken from
  `map2` - so the `map` and the `list` item were not merged!
* All other "non-overlapping" items were added (e.g. `map2Item`)

For **result2** the merge was applied as follows:
* each value for a key present in `map1` and `map2` was taken from
  `map1` - so the `map` and the `list` item were not merged!
* All other "non-overlapping" items were added (e.g. `map1Item` and `mapMergeMode`)


### MapMergeMode scope

The MapMergeMode can only be defined in items of the type
`java.util.Map` and they are only valid for the current map item /
level.

```groovy
map1 = [merge: [key_merge: ["map1"]], skip: [key_skip: ["map1"], (MAP_MERGE_MODE): MapMergeMode.SKIP], replace: [key_replace: ["map1"], (MAP_MERGE_MODE): MapMergeMode.REPLACE]]
map2 = [merge: [key_merge: ["map2"]], skip: [key_skip: ["map2"]], replace: [key_replace: ["map2"]]]
result1 = MapUtils.merge(map1, map2)
result2 = MapUtils.merge(map2, map1)
/*
result1 = [merge: [key_merge: ["map1", "map2"]], skip: [key_skip:["map1"], mapMergeMode:SKIP], replace: [key_replace: ["map2"], mapMergeMode: "REPLACE"]]
result2 = [merge: [key_merge: ["map2", "map1"]], skip: [key_skip:["map2"], mapMergeMode:SKIP], replace: [key_replace: ["map1"], mapMergeMode: "REPLACE"]]
*/
```

* The `merge` item used the default merge mode (`MapMergeMode.MERGE`)
* The `skip` item used the merge mode (`MapMergeMode.SKIP`)
  * In `result1` the values from the incoming map `map2` were skipped so
    the values from `map1` were retained
  * In `result2` the values from the incoming map `map1` were skipped so
    the values from `map2` were retained
* The `replace` item used the merge mode (`MapMergeMode.REPLACE`)
  * In `result1` the values from the incoming map `map2` were used so
    the values from `map1` were replaced
  * In `result2` the values from the incoming map `map1` were used so
    the values from `map2` were replaced