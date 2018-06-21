package io.wcm.testing.jenkins.pipeline.plugins

import com.lesfurets.jenkins.unit.PipelineTestHelper
import io.wcm.testing.jenkins.pipeline.EnvActionImplMock
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile
import org.junit.Assert

import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILE
import static io.wcm.testing.jenkins.pipeline.StepConstants.CONFIGFILEPROVIDER

class ConfigFileProviderPluginMock {

  /**
   * Reference to PipelineTestHelper
   */
  protected PipelineTestHelper helper

  /**
   * Utility for recording executed steps
   */
  protected StepRecorder stepRecorder

  /**
   * Environment
   */
  protected EnvActionImplMock envVars

  protected String workSpaceTmp

  ConfigFileProviderPluginMock(PipelineTestHelper helper, StepRecorder stepRecorder, EnvActionImplMock envVars, String workSpaceTmp) {
    this.helper = helper
    this.stepRecorder = stepRecorder
    this.envVars = envVars
    this.workSpaceTmp = workSpaceTmp

    helper.registerAllowedMethod(CONFIGFILE, [Map.class], configFileCallback)
    helper.registerAllowedMethod(CONFIGFILEPROVIDER, [List.class, Closure.class], configFileProviderCallback)
  }

  /**
   * Mocks the 'configFileProvider' step. For each ManagedFile the environment variable is set to a dummy filepath
   */
  def configFileProviderCallback = { List<ManagedFile> configFiles, Closure closure ->
    stepRecorder.record(CONFIGFILEPROVIDER, configFiles)
    configFiles.each { ManagedFile file ->
      String filePath = file.getTargetLocation()
      if (filePath == null || filePath.isEmpty()) {
        filePath = this.workSpaceTmp.concat(file.fileId)
      }
      file.setTargetLocation(filePath)
      if (file.getVariable() != null && file.getVariable().length() > 0) {
        Exception catchedException = null
        try {
          if (this.envVars.getProperty(file.getVariable()) != null) {
            throw new Exception("${file.getVariable()} is already registered!")
          }
        } catch (Exception e) {
          catchedException = e
        }
        Assert.assertNull("The config provider already has a configFile with variable " + file.getVariable(), catchedException)
        this.envVars.setProperty(file.getVariable(), filePath)
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
    stepRecorder.record(CONFIGFILE, map)
    return new ManagedFile((String) map.fileId, (String) map.targetLocation, (String) map.variable)
  }
}
