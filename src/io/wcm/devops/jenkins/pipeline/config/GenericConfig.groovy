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

import io.wcm.devops.jenkins.pipeline.model.PatternMatchable

/**
 * Object for storing a generic configuration
 */
class GenericConfig extends PatternMatchable implements Serializable {

  private static final long serialVersionUID = 1L

  Object config = null

  GenericConfig(String pattern, String id, Object config) {
    super(pattern, id)
    // set config to empty map when necessary
    if (config == null) {
      config = [:]
    }
    this.config = config
  }

  Object get(String key, Object defaultValue = null) {
    Object ret = defaultValue
    if (this.config != null && this.config[key] != null) {
      ret = this.config[key]
    }
    return ret
  }

}
