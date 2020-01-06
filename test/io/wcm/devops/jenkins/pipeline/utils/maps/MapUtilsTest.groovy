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

import org.junit.Test

import static org.junit.Assert.assertEquals
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

class MapUtilsTest {

  @Test
  void shouldReturnEmptyMap() {
    Map expected = [:]
    Map actual = MapUtils.merge()
    assertEquals(expected, actual)
  }

  @Test
  void shouldReturnInputMap() {
    Map expected = [
        node1: [
            subnode11: [
                prop111: "value111",
                prop112: "value112",
            ],
            prop1    : 1
        ],
        node2: [
            prop2    : 2,
            subnode21: [
                prop21: "value21"
            ]
        ]
    ]
    Map actual = MapUtils.merge(expected)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeTwoMaps() {
    Map map1 = [
        node1: [
            subnode11: [
                prop111: "value111",
                prop112: "value112",
            ],
            prop1    : 1
        ],
        node2: [
            prop1    : 21,
            subnode21: [
                prop21: "value21"
            ]
        ]
    ]
    Map map2 = [
        node1: [
            subnode11: [
                prop111: "value111NEW",
                prop113: "value113"
            ],
            prop2    : 12
        ],
        node2: [
            prop1: "21NEW",
        ]
    ]

    Map expected = [
        node1: [
            subnode11: [
                prop111: "value111NEW",
                prop112: "value112",
                prop113: "value113"
            ],
            prop1    : 1,
            prop2    : 12
        ],
        node2: [
            prop1    : "21NEW",
            subnode21: [
                prop21: "value21"
            ]
        ]
    ]

    Map actual = MapUtils.merge(map1, map2)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeThreeMaps() {
    Map map1 = [
        node1   : [
            subnode: [prop: "map1node1prop"]
        ],
        node2   : [
            subnode: [prop1: "value1"]
        ],
        map1prop: "value1"
    ]
    Map map2 = [
        node1   : [
            subnode: [prop: "map2node1prop"]
        ],
        node2   : [
            subnode: [prop2: "value2"]
        ],
        map2prop: "value2"
    ]
    Map map3 = [
        node1   : [
            subnode: [prop: "map3node1prop"]
        ],
        node2   : [
            subnode: [prop3: "value3"]
        ],
        map3prop: "value3"
    ]

    Map expected = [
        node1   : [
            subnode: [prop: "map3node1prop"]
        ],
        node2   : [
            subnode: [
                prop1: "value1",
                prop2: "value2",
                prop3: "value3",
            ]
        ],
        map1prop: "value1",
        map2prop: "value2",
        map3prop: "value3"
    ]

    Map actual = MapUtils.merge(map1, map2, map3)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeTwoDeepNestedMaps() {
    Map map1 = [l1: [l2: [l3: [l4: [l5: [l5p1: "l5v1"]], l4p1: "l4v1", l4m1p1: "l4m1v1"]]]]
    Map map2 = [l1: [l2: [l3: [l4: [l5: [l5p1: "l5v1NEW"]], l4p1: "l4v1NEW", l4m2p1: "l4m2v1"]]]]
    Map expected = [l1: [l2: [l3: [l4: [l5: [l5p1: "l5v1NEW"]], l4p1: "l4v1NEW", l4m1p1: "l4m1v1", l4m2p1: "l4m2v1"]]]]
    Map actual = MapUtils.merge(map1, map2)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeSimpleLists() {
    Map map1 = [maven: [goals: ["clean", "install"]]]
    Map map2 = [maven: [goals: ["install"]]]
    Map map3 = [maven: [goals: ["site"]]]

    Map expected = [maven: [goals: ["clean", "install", "site"]]]
    Map actual = MapUtils.merge(map1, map2, map3)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeMapLists() {
    Map map1 = [
        node1   : [
            subnode: [prop: [[name: "map1"], [name: "map2"]]]
        ],
        node2   : [
            subnode: [prop1: "value1"]
        ],
        map1prop: "value1"
    ]
    Map map2 = [
        node1   : [
            subnode: [prop: [[name: "map2"], [name: "map3"]]]
        ],
        node2   : [
            subnode: [prop2: "value2"]
        ],
        map2prop: "value2"
    ]
    Map map3 = [
        node1   : [
            subnode: [prop: [[name: "map1"], [name: "map3"], [name: "map4"]]]
        ],
        node2   : [
            subnode: [prop3: "value3"]
        ],
        map3prop: "value3"
    ]

    Map expected = [
        node1   : [
            subnode: [prop: [[name: "map1"], [name: "map2"], [name: "map3"], [name: "map4"]]]
        ],
        node2   : [
            subnode: [
                prop1: "value1",
                prop2: "value2",
                prop3: "value3",
            ]
        ],
        map1prop: "value1",
        map2prop: "value2",
        map3prop: "value3"
    ]

    Map actual = MapUtils.merge(map1, map2, map3)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeMapWithInt() {
    Map map1 = [ "simple" : 1, "list" : [1], "mapItem" : [ "key" : "map1Value" ] ]
    Map map2 = [ "simple" : 2, "list" : 2, "mapItem" : 2 ]
    Map actual1 = MapUtils.merge(map1, map2)
    Map actual2 = MapUtils.merge(map2, map1)

    assertEquals([ "simple" : 2, "list" : 2, "mapItem" : 2 ], actual1)
    assertEquals([ "simple" : 1, "list" : [1], "mapItem" : [ "key" : "map1Value" ] ], actual2)
  }

  @Test
  void shouldNotManipulateSourceMaps() {
    Map config = [
      (ANSIBLE)   : [
        (ANSIBLE_EXTRA_PARAMETERS): [""],
      ],
    ]
    Map configRef = [
      (ANSIBLE)   : [
        (ANSIBLE_EXTRA_PARAMETERS): [""],
      ],
    ]

    Map ansibleGalaxyCfg = [
      (ANSIBLE): [
        (ANSIBLE_EXTRA_PARAMETERS): ["-v"],
      ]
    ]

    Map ansiblePlayBookCfg = [
      (ANSIBLE): [
        (ANSIBLE_PLAYBOOK)     : "playbook",
        (ANSIBLE_EXTRA_VARS)   : [:],
        (ANSIBLE_INJECT_PARAMS): true,
        (ANSIBLE_SKIPPED_TAGS) : [],
      ]
    ]

    ansibleGalaxyCfg = MapUtils.merge(config, ansibleGalaxyCfg)
    ansiblePlayBookCfg = MapUtils.merge(config, ansiblePlayBookCfg)

    assertEquals(configRef, config)
    assertEquals([""],config[ANSIBLE][ANSIBLE_EXTRA_PARAMETERS])
  }

}
