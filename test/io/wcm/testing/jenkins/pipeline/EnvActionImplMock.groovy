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

import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder

/**
 * Mock for EnvActionImpl to support setProperty and getProperty on env Var
 */
class EnvActionImplMock extends GroovyObjectSupport {

  protected Map env

  protected StepRecorder stepRecorder

  EnvActionImplMock() {
    env = new TreeMap<String, String>()
  }

  EnvActionImplMock(StepRecorder stepRecorder) {
    env = new TreeMap<String, String>()
    this.stepRecorder = stepRecorder
  }

  Map getEnvironment() throws IOException, InterruptedException {
    return env
  }

  @Override
  String getProperty(String propertyName) {
    if (stepRecorder) {
      stepRecorder.record(StepConstants.ENV_GET_PROPERTY, propertyName)
    }
    return env.getOrDefault(propertyName, null)
  }

  @Override
  void setProperty(String propertyName, Object newValue) {

    if (newValue != null) {
      if (stepRecorder) {
        stepRecorder.record(StepConstants.ENV_SET_PROPERTY, String.valueOf(newValue))
      }
      env.put(propertyName, String.valueOf(newValue))
    } else {
      if (stepRecorder) {
        stepRecorder.record(StepConstants.ENV_SET_PROPERTY, null)
      }
      env.remove(propertyName)
    }
  }

}
