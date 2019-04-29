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

import org.junit.Test
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import static org.junit.Assert.assertEquals

class MapMergeModeTest {

  Map existingConfig = [
    key: [
      string : "existing",
      boolean: false,
      list   : ["existingItem1", "existingItem2"],
      integer: 0,
      map    : [existingP1: "existingV1", existingP2: "existingV2"],
    ]
  ]

  Map incomingConfig = [
    key: [
      string : "custom",
      boolean: true,
      list   : ["incomingItem1", "incomingItem2"],
      integer: 1,
      map    : [incomingP1: "incomingV1", incomingP2: "incomingV2"],
    ]
  ]

  @Test
  void shouldMergeSimpleWithModeReplace() {
    existingConfig['key'][MAP_MERGE_MODE] = MapMergeMode.REPLACE
    Map expectedConfig = [
      key: [
        string          : "custom",
        boolean         : true,
        list            : ["incomingItem1", "incomingItem2"],
        integer         : 1,
        map             : [incomingP1: "incomingV1", incomingP2: "incomingV2"],
        (MAP_MERGE_MODE): MapMergeMode.REPLACE
      ]
    ]
    Map actualConfig = MapUtils.merge(existingConfig, incomingConfig)
    assertEquals(expectedConfig, actualConfig)
  }

  @Test
  void shouldMergeSimpleWithModeSkip() {
    existingConfig['key'][MAP_MERGE_MODE] = MapMergeMode.SKIP
    Map expectedConfig = [
      key: [
        string          : "existing",
        boolean         : false,
        list            : ["existingItem1", "existingItem2"],
        integer         : 0,
        map             : [existingP1: "existingV1", existingP2: "existingV2"],
        (MAP_MERGE_MODE): MapMergeMode.SKIP
      ]
    ]
    Map actualConfig = MapUtils.merge(existingConfig, incomingConfig)
    assertEquals(expectedConfig, actualConfig)
  }

  @Test
  void shouldMergeSimpleWithModeMerge() {
    existingConfig['key'][MAP_MERGE_MODE] = MapMergeMode.MERGE
    Map expectedConfig = [
      key: [
        string          : "custom",
        boolean         : true,
        list            : ["existingItem1", "existingItem2", "incomingItem1", "incomingItem2"],
        integer         : 1,
        map             : [existingP1: "existingV1Changed", existingP2: "existingV2", incomingP1: "incomingV1", incomingP2: "incomingV2"],
        (MAP_MERGE_MODE): MapMergeMode.MERGE
      ]
    ]
    // add existing property with changed value into incoming config
    incomingConfig['key']['map']["existingP1"] = "existingV1Changed"
    Map actualConfig = MapUtils.merge(existingConfig, incomingConfig)
    assertEquals(expectedConfig, actualConfig)
  }

  @Test
  void shouldMergeListWithModeSkip() {
    List existing = ["existing1, existing2"]
    List incoming = ["incoming1", "incoming2"]
    List expected = ["existing1, existing2"]
    List actual = MapUtils.mergeList(existing, incoming, MapMergeMode.SKIP)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeListWithModeKeepIncoming() {
    List existing = ["existing1, existing2"]
    List incoming = ["incoming1", "incoming2"]
    List expected = ["incoming1", "incoming2"]
    List actual = MapUtils.mergeList(existing, incoming, MapMergeMode.REPLACE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeListWithModeMerge() {
    List existing = ["existing1, existing2"]
    List incoming = ["incoming1", "incoming2"]
    List expected = ["existing1, existing2", "incoming1", "incoming2"]
    List actual = MapUtils.mergeList(existing, incoming, MapMergeMode.MERGE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeValueWithModeSkip() {
    Boolean existing = false
    Boolean incoming = true
    Boolean expected = false
    Object actual = MapUtils.mergeValue(existing, incoming, MapMergeMode.SKIP)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeValueWithModeReplace() {
    Object existing = "existing"
    Object incoming = "incoming1"
    Object expected = "incoming1"
    Object actual = MapUtils.mergeValue(existing, incoming, MapMergeMode.REPLACE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeValueWithModeMerge() {
    Object existing = 1
    Object incoming = 2
    Object expected = 2
    Object actual = MapUtils.mergeValue(existing, incoming, MapMergeMode.MERGE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeMapWithModeSkip() {
    Map existing = [existing1: "existingValue1", existing2: "existingValue2"]
    Map incoming = [incoming1: "incomingValue1", incoming2: "incomingValue2"]
    Map expected = [existing1: "existingValue1", existing2: "existingValue2"]
    Map actual = MapUtils.mergeMap(existing, incoming, MapMergeMode.SKIP)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeMapWithModeReplace() {
    Map existing = [existing1: "existingValue1", existing2: "existingValue2"]
    Map incoming = [incoming1: "incomingValue1", incoming2: "incomingValue2"]
    Map expected = [incoming1: "incomingValue1", incoming2: "incomingValue2"]
    Map actual = MapUtils.mergeMap(existing, incoming, MapMergeMode.REPLACE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldMergeMapWithModeMerge() {
    Map existing = [existing1: "existingValue1", existing2: "existingValue2"]
    Map incoming = [incoming1: "incomingValue1", incoming2: "incomingValue2", existing1: "existing1ValueChanged"]
    Map expected = [existing1: "existing1ValueChanged", existing2: "existingValue2", incoming1: "incomingValue1", incoming2: "incomingValue2"]
    Map actual = MapUtils.mergeMap(existing, incoming, MapMergeMode.MERGE)
    assertEquals(expected, actual)
  }

  @Test
  void shouldNotOverwriteProtectedModeVariable() {
    assertEquals(MapMergeMode.MERGE, MapUtils.mergeValue(MapMergeMode.MERGE, MapMergeMode.SKIP, MapMergeMode.MERGE))
    assertEquals(MapMergeMode.SKIP, MapUtils.mergeValue(MapMergeMode.SKIP, MapMergeMode.REPLACE, MapMergeMode.REPLACE))
    assertEquals(MapMergeMode.REPLACE, MapUtils.mergeValue(MapMergeMode.REPLACE, MapMergeMode.MERGE, MapMergeMode.SKIP))
  }

  @Test
  void shouldMergeComplexWithMultipleMergeModes() {
    Map existing = [
      "l11": [
        "l21": [
          "map"           : [i1: 1, i2: 2],
          "list"          : [1, 2],
          (MAP_MERGE_MODE): MapMergeMode.REPLACE
        ],
        "l22": [
          "list": [1, 2],
        ],
        "l23": [
          "value"         : "existing",
          "skipped"       : true,
          (MAP_MERGE_MODE): MapMergeMode.SKIP
        ]
      ],
      "l12": [
        map             : [el121: 1, el122: 2],
        list            : ["el121", "el121"],
        bool1           : false,
        i1              : 0,
        (MAP_MERGE_MODE): MapMergeMode.MERGE
      ]
    ]
    Map incoming = [
      "l11": [
        "l21": [
          "map"           : [i1: 2, i2: 3],
          "list"          : [3, 4],
          "replaced"      : true,
          (MAP_MERGE_MODE): MapMergeMode.SKIP // should no be used since it is already in the existing  map
        ],
        "l22": [
          "list"          : [3, 4],
          "merged"        : true,
          (MAP_MERGE_MODE): MapMergeMode.MERGE // should no be used since it is already in the existing  map
        ],
        "l23": [
          "value"         : "incoming",
          "skipped"       : false,
          (MAP_MERGE_MODE): MapMergeMode.MERGE // should no be used since it is already in the existing  map
        ]
      ],
      "l12": [
        map             : [il121: 3, il122: 4],
        list            : ["il121", "il122", "el121"],
        bool1           : true,
        i1              : 1,
        bool2           : false,
        i2              : 2,
        (MAP_MERGE_MODE): MapMergeMode.SKIP // should no be used since it is already in the existing map
      ],
      "l13": [
        il13: 1,
      ]
    ]
    Map expected = [
      "l11": [
        "l21": [
          "map"           : [i1: 2, i2: 3],
          "list"          : [3, 4],
          (MAP_MERGE_MODE): MapMergeMode.REPLACE, // should no be used since it is already in the existing  map
          "replaced"      : true
        ],
        "l22": [
          "list"          : [1, 2, 3, 4],
          "merged"        : true,
          (MAP_MERGE_MODE): MapMergeMode.MERGE
        ],
        "l23": [
          "value"         : "existing",
          "skipped"       : true,
          (MAP_MERGE_MODE): MapMergeMode.SKIP
        ]
      ],
      "l12": [
        map             : [el121: 1, el122: 2, il121: 3, il122: 4],
        list            : ["el121", "el121", "il121", "il122"],
        bool1           : true,
        i1              : 1,
        bool2           : false,
        i2              : 2,
        (MAP_MERGE_MODE): MapMergeMode.MERGE
      ]
      ,
      "l13": [
        il13: 1,
      ]
    ]
    Map actual = MapUtils.merge(existing, incoming)
    assertEquals(expected, actual)
  }

  @Test
  void shouldReturnDefaultScmConfig() {
    Map defaultScmConfig = [
      (SCM):
        [

          (SCM_URL)                                : null,
          (SCM_CREDENTIALS_ID)                     : null,
          (SCM_BRANCHES)                           : [[name: '*/master'], [name: '*/develop']],
          (SCM_SUBMODULE_CONFIG)                   : [],
          (SCM_DO_GENERATE_SUBMODULE_CONFIGURATION): false,
          (SCM_USER_REMOTE_CONFIG)                 : [:],
          (SCM_USER_REMOTE_CONFIGS)                : [],
          (MAP_MERGE_MODE)                         : MapMergeMode.REPLACE
        ]
    ]
    Map incomingScmConfig = [:]
    Map actual = MapUtils.merge(defaultScmConfig, incomingScmConfig)
    assertEquals(defaultScmConfig, actual)
  }

  @Test
  void shouldMergeScmUrl() {
    Map defaultScmConfig = [
      (SCM): [
        (SCM_URL)                                : null,
        (SCM_CREDENTIALS_ID)                     : null,
        (SCM_BRANCHES)                           : [[name: '*/master'], [name: '*/develop']],
        (SCM_SUBMODULE_CONFIG)                   : [],
        (SCM_DO_GENERATE_SUBMODULE_CONFIGURATION): false,
        (SCM_USER_REMOTE_CONFIG)                 : [:],
        (SCM_USER_REMOTE_CONFIGS)                : [],
        (MAP_MERGE_MODE)                         : MapMergeMode.REPLACE
      ]
    ]
    Map incomingScmConfig = [
      (SCM): [
        (SCM_URL)     : "incoming-url",
        (SCM_BRANCHES): [[name: '*/develop']],
      ]
    ]
    Map expectedScmConfig = [
      (SCM): [
        (SCM_URL)                                : "incoming-url",
        (SCM_CREDENTIALS_ID)                     : null,
        (SCM_BRANCHES)                           : [[name: '*/develop']],
        (SCM_SUBMODULE_CONFIG)                   : [],
        (SCM_DO_GENERATE_SUBMODULE_CONFIGURATION): false,
        (SCM_USER_REMOTE_CONFIG)                 : [:],
        (SCM_USER_REMOTE_CONFIGS)                : [],
        (MAP_MERGE_MODE)                         : MapMergeMode.REPLACE
      ]
    ]
    Map actualScmConfig = MapUtils.merge(defaultScmConfig, incomingScmConfig)
    assertEquals(expectedScmConfig, actualScmConfig)
  }

}
