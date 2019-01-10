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
package io.wcm.devops.jenkins.pipeline.job

import com.cwctravel.hudson.plugins.extended_choice_parameter.ExtendedChoiceParameterDefinition
import io.wcm.testing.jenkins.pipeline.CpsScriptTestBase
import org.junit.Test

import static org.junit.Assert.assertEquals

class BuildParameterFactoryTest extends CpsScriptTestBase {

  BuildParameterFactory factory

  ExtendedChoiceParameterDefinition underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    factory = new BuildParameterFactory(this.script)
  }

  @Test
  void shouldCreateDefaultMultiSelectParam() {
    List expectedValueList = ["value1", "value2", "value3"]
    List expectedDefaultValueList = []
    String expectedValues = expectedValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)
    String expectedDefaultValues = expectedDefaultValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)

    underTest = factory.createMultiSelectParameter("multiSelectName", "multiSelectDescription", expectedValueList)
    assertEquals("multiSelectName", underTest.getName())
    assertEquals("multiSelectDescription", underTest.getDescription())
    assertEquals(BuildParameterFactory.PARAMETER_TYPE_MULTI_SELECT, underTest.getType())
    assertEquals(expectedValues, underTest.getValue())
    assertEquals(expectedDefaultValues, underTest.getDefaultValue())
    assertEquals(BuildParameterFactory.DEFAULT_VISIBLE_ITEM_COUNT, underTest.getVisibleItemCount())
  }

  @Test
  void shouldCreateCustomMultiSelectParam() {
    List expectedValueList = ["value1", "value2", "value3", "value4"]
    List expectedDefaultValueList = ["value2", "value3"]
    String expectedValues = expectedValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)
    String expectedDefaultValues = expectedDefaultValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)

    underTest = factory.createMultiSelectParameter("multiSelectName", "multiSelectDescription", expectedValueList, expectedDefaultValueList, 10)
    assertEquals("multiSelectName", underTest.getName())
    assertEquals("multiSelectDescription", underTest.getDescription())
    assertEquals(BuildParameterFactory.PARAMETER_TYPE_MULTI_SELECT, underTest.getType())
    assertEquals(expectedValues, underTest.getValue())
    assertEquals(expectedDefaultValues, underTest.getDefaultValue())
    assertEquals(10, underTest.getVisibleItemCount())
  }

  @Test
  void shouldCreateDefaultMultiCheckboxParam() {
    List expectedValueList = ["value1", "value2", "value3"]
    List expectedDefaultValueList = []
    String expectedValues = expectedValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)
    String expectedDefaultValues = expectedDefaultValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)

    underTest = factory.createMultiCheckboxParameter("multiCheckboxName", "multiCheckboxDescription", expectedValueList)
    assertEquals("multiCheckboxName", underTest.getName())
    assertEquals("multiCheckboxDescription", underTest.getDescription())
    assertEquals(BuildParameterFactory.PARAMETER_TYPE_CHECK_BOX, underTest.getType())
    assertEquals(expectedValues, underTest.getValue())
    assertEquals(expectedDefaultValues, underTest.getDefaultValue())
    assertEquals(BuildParameterFactory.DEFAULT_VISIBLE_ITEM_COUNT, underTest.getVisibleItemCount())
  }

  @Test
  void shouldCreateCustomMultiCheckboxParam() {
    List expectedValueList = ["value1", "value2", "value3", "value4"]
    List expectedDefaultValueList = ["value2", "value3"]
    String expectedValues = expectedValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)
    String expectedDefaultValues = expectedDefaultValueList.join(BuildParameterFactory.SELECTOR_DELIMITER)

    underTest = factory.createMultiCheckboxParameter("multiCheckboxName", "multiCheckboxDescription", expectedValueList, expectedDefaultValueList, 10)
    assertEquals("multiCheckboxName", underTest.getName())
    assertEquals("multiCheckboxDescription", underTest.getDescription())
    assertEquals(BuildParameterFactory.PARAMETER_TYPE_CHECK_BOX, underTest.getType())
    assertEquals(expectedValues, underTest.getValue())
    assertEquals(expectedDefaultValues, underTest.getDefaultValue())
    assertEquals(10, underTest.getVisibleItemCount())
  }

}
