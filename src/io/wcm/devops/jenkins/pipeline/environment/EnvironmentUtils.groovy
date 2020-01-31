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
package io.wcm.devops.jenkins.pipeline.environment

import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

class EnvironmentUtils implements Serializable {

  private static final long serialVersionUID = 1L

  transient Script script

  GroovyObjectSupport envActionImpl

  Logger log = new Logger(this)

  EnvironmentUtils(Script script) {
    this.script = script
    this.envActionImpl = script.env
  }


  Boolean setEnvWhenEmpty(String name, Object value) {
    Object existing = this.envActionImpl.getProperty(name)
    if (existing == null) {
      log.debug("setEnvWhenEmpty set, name: '$name', value: '$value', existing: '$existing'")
      this.envActionImpl.setProperty(name, value)
      return true
    }
    log.debug("setEnvWhenEmpty not set, name: '$name', value: '$value', existing: '$existing'")

    return false
  }

  Object getFirstFound(String[] names) {
    Object value = null
    for (String name in names) {
      value = this.envActionImpl.getProperty(name)
      log.debug("getFirstFound -> search for '$name', value, ", value)
      if (value != null) {
        break
      }
    }
    return value
  }


}
