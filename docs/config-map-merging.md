# Configuration map merging

The jenkins-pipeline-library contains the helper
[`MapUtils.groovy`](../src/io/wcm/devops/jenkins/pipeline/utils/maps/MapUtils.groovy)
which is heavily used to integrate custom changes into the configuration
map.

`MapUtils.groovy` will merge 2-n Maps together and returns the merged
result without modifying the original maps.

# Examples

## Example 1: One level map

This example merges a map with one level with the default merge mode
`MapMergeMode.MERGE`.

```groovy
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

Map ex1Map1 = [
  prop1 : "value1",
  prop2 : "value2_from_map1",
  list1 : ["item1_1", "item1_2"],
  list2 : ["item1", "item2"]
]
Map ex1Map2 = [
  prop2 : "value2_from_map2",
  prop3 : "value3",
  list1 : ["item1_3"],
  list2 : "non_list_values_overwrite_lists"
]
// merge ex1Map2 into ex1Map1
Map ex1ResultVariant1 = MapUtils.merge(ex1Map1, ex1Map2)
// merge ex1Map1 into ex1Map2
Map ex1ResultVariant2 = MapUtils.merge(ex1Map2, ex1Map1)

// ex1ResultVariant1
ex1ResultVariant1 = [
  prop1: "value1", 
  prop2: "value2_from_map2", // overwritten from ex1Map2
  list1: [ 
    "item1_1", "item1_2", "item1_3" // "item1_3" was added from ex1Map2
  ], 
  "list2" : "non_list_values_overwrite_lists", // when values from the "map to the right" can not be merged they overwrite
  "prop3" : "value3"
]

ex1ResultVariant2 = [
  prop2 : "value2_from_map1", 
  prop3 : "value3", 
  list1 : [
    "item1_3", 
    "item1_1", 
    "item1_2"
  ], 
  list2 : [
    "item1", "item2" //  // when values from the "map to the right" can not be merged they overwrite
  ], 
  prop1 : "value1"
]
```

## Example 2: Multilevel merge

This example merges a multi level map with the default
merge mode `MapMergeMode.MERGE`.

```groovy
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils

Map ex2Map1 = [
  "level1_1" : "value_from_ex2Map1",
  "level1_2" : [
    "level2_1": "value_from_ex2Map1",
    "level2_2": ["item1", "item2"],
    "level2_3": [
      "level3_1": "value_from_ex2Map1",
      "level3_2": ["item3_2_1", "item3_2_2"],
    ]
  ]
]
Map ex2Map2 = [
  "level1_1" : "value_from_ex2Map2",
  "level1_2" : [
    "level2_1": "value_level2_1_from_ex2Map2",
    "level2_2": "non_list_values_overwrite_lists",
    "level2_3": [
      "level3_1": "value_from_ex2Map2",
      "level3_2": ["item3_2_1", "item3_2_2"],
      "level3_3": [
        "map" : "from_ex2Map2"
      ]
    ]
  ],
  "level1_3" : [
    "level1_1" : "value"
  ]
]

// Merge ex2Map2 into ex2Map1
Map ex2ResultVariant1 = MapUtils.merge(ex2Map1, ex2Map2)
// Merge ex2Map1 into ex2Map2
Map ex2ResultVariant2 = MapUtils.merge(ex2Map2, ex2Map1)

ex2ResultVariant1 = [
  level1_1 : value_from_ex2Map2, 
  level1_2 : [
    level2_1 : value_l2_1_from_ex2Map2, 
    level2_2 : non_list_values_overwrite_lists, 
    level2_3 : [
      level3_1 :value_from_ex2Map2,
      level3_2 : [ 
        item3_2_1, 
        item3_2_2
      ], 
      level3_3 : [ 
        map : from_ex2Map2
      ]
    ]
  ],
  level1_3 : [ 
    level1_1 : value
  ]
]
ex2ResultVariant2 = [
  level1_1 : value_from_ex2Map1, 
  level1_2 : [
    level2_1 : value_from_ex2Map1, 
    level2_2 : [
      item1, item2
    ], level2_3:[level3_1:value_from_ex2Map1, level3_2:[item3_2_1, item3_2_2], level3_3:[map:from_ex2Map2]]], level1_3:[level1_1:value]
]

```