/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.config

import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import io.wcm.devops.jenkins.pipeline.utils.resources.YamlLibraryResource
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import net.sf.json.JSON
import static org.junit.Assert.*
import org.junit.Test

class GenericConfigParserTest extends DSLTestBase {

  YamlLibraryResource yamlLibraryResource
  Object testContent

  GenericConfigParser underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    yamlLibraryResource = new YamlLibraryResource(this.dslMock.getMock(), "jenkins-pipeline-library/config/generic-config-parser-testdata.yaml")
    testContent = yamlLibraryResource.load()
    underTest = new GenericConfigParser()
  }

  @Test
  void shouldLoadAndParseFromYaml() {
    List<GenericConfig> genericConfigs = underTest.parse(testContent)
    assertEquals(3, genericConfigs.size())

    GenericConfig item1 = genericConfigs[0]
    assertEquals("multi-pattern-id", item1.getId())
    assertEquals("multi-pattern1", item1.getPattern())

    assertEquals(null, item1.get('patterns'))
    assertEquals(null, item1.get('id'))
    assertEquals("value", item1.get('prop'))
    assertEquals(["item1", "item2"], item1.get('list'))
    assertEquals(["subdict1": "subvalue1", "subdict2": "subvalue2"], item1.get('dict'))

    GenericConfig item2 = genericConfigs[1]
    assertEquals("multi-pattern-id", item2.getId())
    assertEquals("multi-pattern2", item2.getPattern())

    assertEquals(null, item2.get('patterns'))
    assertEquals(null, item2.get('id'))
    assertEquals("value", item2.get('prop'))
    assertEquals(["item1", "item2"], item2.get('list'))
    assertEquals(["subdict1": "subvalue1", "subdict2": "subvalue2"], item2.get('dict'))

    GenericConfig item3 = genericConfigs[2]
    assertEquals("single-pattern-id", item3.getId())
    assertEquals("single-pattern", item3.getPattern())

    assertEquals(null, item3.get('pattern'))
    assertEquals(null, item3.get('id'))
    assertEquals("value_2", item3.get('prop_2'))
    assertEquals(["item1_2", "item2_2"], item3.get('list_2'))
    assertEquals(["subdict1_2": "subvalue1_2", "subdict2_2": "subvalue2_2"], item3.get('dict_2'))
  }
}
