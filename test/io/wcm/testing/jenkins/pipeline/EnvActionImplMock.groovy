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
package io.wcm.testing.jenkins.pipeline

/**
 * Mock for EnvActionImpl to support setProperty and getProperty on env Var
 */
class EnvActionImplMock extends GroovyObjectSupport {

  protected Map env

  EnvActionImplMock() {
    env = new TreeMap<String, String>()
  }

  Map getEnvironment() throws IOException, InterruptedException {
    return env
  }

  @Override
  String getProperty(String propertyName) {
    return env.getOrDefault(propertyName, null)
  }

  @Override
  void setProperty(String propertyName, Object newValue) {
    if (newValue != null) {
      env.put(propertyName, String.valueOf(newValue))
    } else {
      env.remove(propertyName)
    }
  }

}
