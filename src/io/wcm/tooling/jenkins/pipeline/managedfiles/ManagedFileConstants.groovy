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
package io.wcm.tooling.jenkins.pipeline.managedfiles

/**
 * Constants for managed files used by the pipeline library
 */
class ManagedFileConstants implements Serializable {

  private static final long serialVersionUID = 1L

  static final String GLOBAL_MAVEN_SETTINGS_PATH = "managedfiles/maven/global-settings.json"
  static final String GLOBAL_MAVEN__SETTINGS_ENV = "MVN_GLOBAL_SETTINGS"

  static final String MAVEN_SETTINS_PATH = "managedfiles/maven/settings.json"
  static final String MAVEN_SETTING_ENV = "MVN_SETTINGS"

  static final String NPM_CONFIG_USERCONFIG_PATH = "managedfiles/npm/npm-config-userconfig.json"
  // wrong named in existing jenkins/maven projects
  // TODO: remove
  static final String NPM_CONFIG_USERCONFIG_ENV = "NPM_CONFIG_USERCONFIG"
  // correct name based on NPM config
  static final String NPM_CONF_USERCONFIG_ENV = "NPM_CONF_USERCONFIG"

  static final String NPMRC_PATH = "managedfiles/npm/npmrc.json"
  // wrong name in existing jenkins/maven projects
  // TODO: remove
  static final String NPMRC_ENV = "NPMRC"
  // correct name based on NPM config
  static final String NPM_CONF_GLOBALCONFIG_ENV = "NPM_CONF_GLOBALCONFIG"


  static final String BUNDLE_CONFIG_ENV = "BUNDLE_CONFIG"
  static final String BUNDLE_CONFIG_PATH = "managedfiles/ruby/bundle-config.json"

}
