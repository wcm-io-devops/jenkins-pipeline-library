/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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
package io.wcm.testing.jenkins.pipeline.plugins.credentials

import com.lesfurets.jenkins.unit.PipelineTestHelper
import io.wcm.testing.jenkins.pipeline.EnvActionImplMock
import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder

import static io.wcm.testing.jenkins.pipeline.StepConstants.USERNAME_PASSWORD
import static io.wcm.testing.jenkins.pipeline.StepConstants.WITH_CREDENTIALS

/**
 * Mock for Credentials Plugin
 */
class CredentialsPluginMock {

  /**
   * Mocked username password credentials
   */
  protected Map mockedUsernamePasswordCredentials = [:]

  LibraryIntegrationTestContext context

  CredentialsPluginMock(LibraryIntegrationTestContext context) {
    this.context = context

    context.getPipelineTestHelper().registerAllowedMethod(USERNAME_PASSWORD, [Map.class], usernamePasswordCallback)
    context.getPipelineTestHelper().registerAllowedMethod(WITH_CREDENTIALS, [List.class, Closure.class], withCredentialsCallback)
  }

  void mockUsernamePassword(String credentialsId, String username, String password) {
    UsernamePasswordMock mockedCredentials = new UsernamePasswordMock(credentialsId: credentialsId, username: username, password: password)
    this.mockedUsernamePasswordCredentials[credentialsId] = mockedCredentials
  }

  /**
   * Callback for addBadge step
   */
  def usernamePasswordCallback = {
    Map recordData ->
      String credentialsId = recordData['credentialsId']
      String passwordVariable = recordData['passwordVariable']
      String usernameVariable = recordData['usernameVariable']
      context.getStepRecorder().record(USERNAME_PASSWORD, recordData)
      return new UsernamePasswordMock(credentialsId: credentialsId, passwordVariable: passwordVariable, usernameVariable: usernameVariable)
  }

  /**
   * Mock for withCredentialsCallback
   */
  def withCredentialsCallback = {
    List credentials, Closure body ->
      List modifiedEnvVars = []
      // retrieve credentials, provide them in envVars
      for (Object credential in credentials) {
        if (credential instanceof UsernamePasswordMock) {
          String username = "MOCKED_USERNAME"
          String password = "MOCKED_PASSWORD"
          // try to retrieve the mocked credentials
          UsernamePasswordMock mockedCredentials = this.mockedUsernamePasswordCredentials[credential.getCredentialsId()] ?: null
          if (mockedCredentials) {
            username = mockedCredentials.getUsername()
            password = mockedCredentials.getPassword()
          }
          modifiedEnvVars.push(credential.getUsernameVariable())
          modifiedEnvVars.push(credential.getPasswordVariable())
          this.context.getEnvVars().setProperty(credential.getUsernameVariable(), username)
          this.context.getEnvVars().setProperty(credential.getPasswordVariable(), password)
        }
      }
      // call the body
      body.call()
      // reset environment variables
      for (String modifiedEnvVar in modifiedEnvVars) {
        this.context.getEnvVars().setProperty(modifiedEnvVar, null)
      }
  }
}
