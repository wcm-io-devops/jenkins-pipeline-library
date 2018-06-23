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
package io.wcm.testing.jenkins.pipeline.plugins

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestContext
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile
import org.junit.Assert

import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILE
import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILEPROVIDER

class ConfigFileProviderPluginMock {

  LibraryIntegrationTestContext context

  ConfigFileProviderPluginMock(LibraryIntegrationTestContext context) {
    this.context = context

    context.getPipelineTestHelper().registerAllowedMethod(CONFIGFILE, [Map.class], configFileCallback)
    context.getPipelineTestHelper().registerAllowedMethod(CONFIGFILEPROVIDER, [List.class, Closure.class], configFileProviderCallback)
  }

  /**
   * Mocks the 'configFileProvider' step. For each ManagedFile the environment variable is set to a dummy filepath
   */
  def configFileProviderCallback = { List<ManagedFile> configFiles, Closure closure ->
    context.getStepRecorder().record(CONFIGFILEPROVIDER, configFiles)
    configFiles.each { ManagedFile file ->
      String filePath = file.getTargetLocation()
      if (filePath == null || filePath.isEmpty()) {
        filePath = context.WORKSPACE_TMP_PATH.concat(file.fileId)
      }
      file.setTargetLocation(filePath)
      if (file.getVariable() != null && file.getVariable().length() > 0) {
        Exception catchedException = null
        try {
          if (context.getEnvVars().getProperty(file.getVariable()) != null) {
            throw new Exception("${file.getVariable()} is already registered!")
          }
        } catch (Exception e) {
          catchedException = e
        }
        Assert.assertNull("The config provider already has a configFile with variable " + file.getVariable(), catchedException)
        context.getEnvVars().setProperty(file.getVariable(), filePath)
      }
    }
    closure.run()
  }

  /**
   * Mocks the 'configFile' step
   *
   * @return a new ManagedFile object with the arguments provided in the Map
   */
  def configFileCallback = { Map map ->
    context.getStepRecorder().record(CONFIGFILE, map)
    return new ManagedFile((String) map.fileId, (String) map.targetLocation, (String) map.variable)
  }
}
