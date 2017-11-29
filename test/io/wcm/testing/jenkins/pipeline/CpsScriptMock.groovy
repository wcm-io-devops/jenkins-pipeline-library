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

import org.jenkinsci.plugins.workflow.cps.CpsScript

class CpsScriptMock extends CpsScript {

  /**
   * DSL Mock
   */
  private DSLMock dslMock

  /**
   * Environment variables
   */
  private EnvActionImplMock envVars

  CpsScriptMock() {
    super()
    dslMock = new DSLMock()
    envVars = new EnvActionImplMock()
    binding.setVariable(CpsScript.STEPS_VAR, dslMock.getMock())
    binding.setVariable('env', envVars)
  }

  @Override
  Object run() {
    return null
  }

  /**
   * Getter function for logMessages object
   *
   * @return the recorded logMessages
   */
  List<String> getLogMessages() {
    return dslMock.getLogMessages()
  }

  /**
   * Returns the value of an environment variable
   *
   * @param var The name of the environment variable to return
   * @return The value of the environment variable
   */
  public getEnv(String var) {
    return this.envVars.getProperty(var)
  }

  /**
   * Sets an environment variable
   *
   * @param var The name of the environment variable
   * @param value The value of the environment variable
   */
  public setEnv(String var, String value) {
    this.envVars.setProperty(var, value)
  }

  /**
   * Getter function for DSL mock
   *
   * @return dslMock
   */
  DSLMock getDslMock() {
    return dslMock
  }
}
