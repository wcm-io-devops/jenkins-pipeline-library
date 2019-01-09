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

/**
 * This eases the use of some parameter plugins like the Extended Choice Parameter plugin
 * In order to use this factory you have to approve the script signature in the Jenkins script approval
 */
class BuildParameterFactory {

  public static final String PARAMETER_TYPE_MULTI_SELECT = "PT_MULTI_SELECT"
  public static final String PARAMETER_TYPE_CHECK_BOX = "PT_CHECKBOX"
  public static final String SELECTOR_DELIMITER = ","

  public static final Integer DEFAULT_VISIBLE_ITEM_COUNT = 5

  /**
   * Creates a multi select parameter
   *
   * @param name The name of the parameter
   * @param description The description of the parameter
   * @param options The available options
   * @param defaultValues The default selected values
   * @param visibleItemCount Maximum visible items
   * @return The created ExtendedChoiceParameterDefinition
   */
  ExtendedChoiceParameterDefinition createMultiSelectParameter(String name, String description, List<String> options, List<String> defaultValues = [], Integer visibleItemCount = BuildParameterFactory.DEFAULT_VISIBLE_ITEM_COUNT) {
    return createParameter(PARAMETER_TYPE_MULTI_SELECT, name, description, options, defaultValues, visibleItemCount)
  }

  /**
   * Creates a multi checkbox parameter
   *
   * @param name The name of the parameter
   * @param description The description of the parameter
   * @param options The available options
   * @param defaultValues The default selected values
   * @param visibleItemCount Maximum visible items
   * @return The created ExtendedChoiceParameterDefinition
   */
  ExtendedChoiceParameterDefinition createMultiCheckboxParameter(String name, String description, List<String> options, List<String> defaultValues = [], Integer visibleItemCount = BuildParameterFactory.DEFAULT_VISIBLE_ITEM_COUNT) {
    return createParameter(PARAMETER_TYPE_CHECK_BOX, name, description, options, defaultValues, visibleItemCount)
  }

  /**
   * Creates a ExtendedChoiceParameterDefinition with the given type
   *
   * @param type The type of the ExtendedChoiceParameterDefinition to create
   * @param name The name of the parameter
   * @param description The description of the parameter
   * @param options The available options
   * @param defaultValues The default selected values
   * @param visibleItemCount Maximum visible items
   *
   * @return The created ExtendedChoiceParameterDefinition
   */
  private ExtendedChoiceParameterDefinition createParameter(String type, String name, String description, List<String> options, List<String> defaultValues = [], Integer visibleItemCount = BuildParameterFactory.DEFAULT_VISIBLE_ITEM_COUNT) {
    return new ExtendedChoiceParameterDefinition(
      name,
      type,
      options.join(SELECTOR_DELIMITER),
      null, // project name
      null, // propertyFile
      null, // groovyScript
      null, // groovyScriptFile
      null, // bindings
      null, // groovyClassPath
      null, // propertykey
      defaultValues.join(SELECTOR_DELIMITER), //default property value
      null, //defaultPropertyFile
      null, //defaultGroovyScript
      null, //defaultGroovyScriptFile
      null, //default bindings
      null, //defaultGroovyClasspath
      null, //defaultPropertyKey
      null, //descriptionPropertyValue
      null, //descriptionPropertyFile
      null, //descriptionGroovyScript
      null, //descriptionGroovyScriptFile
      null, //descriptionBindings
      null, //descriptionGroovyClasspath
      null, //descriptionPropertyKey
      null,// javascript file
      null, // javascript
      false, // save json param to file
      false, // quote
      visibleItemCount, // visible item count
      description,
      SELECTOR_DELIMITER)
  }
}
