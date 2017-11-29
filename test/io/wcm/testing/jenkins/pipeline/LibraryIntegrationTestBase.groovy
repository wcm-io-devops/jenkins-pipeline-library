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

import com.lesfurets.jenkins.unit.BasePipelineTest
import hudson.AbortException
import hudson.model.Run
import io.wcm.testing.jenkins.pipeline.global.lib.SelfSourceRetriever
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile
import org.junit.Assert
import org.junit.Before
import org.jvnet.hudson.tools.versionnumber.VersionNumberBuildInfo
import org.jvnet.hudson.tools.versionnumber.VersionNumberCommon
import org.jvnet.hudson.tools.versionnumber.VersionNumberStep

import java.util.regex.Pattern

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static io.wcm.testing.jenkins.pipeline.StepConstants.*
import static org.mockito.Mockito.mock

/**
 * Base class for integration tests that use the JenkinsPipelineUnit testing framework
 *
 * @see <a href="https://github.com/lesfurets/JenkinsPipelineUnit">JenkinsPipelineUnit</a>
 */
class LibraryIntegrationTestBase extends BasePipelineTest {

  public final static String WORKSPACE_PATH = "/path/to/workspace"
  public final static String WORKSPACE_TMP_PATH = WORKSPACE_PATH.concat("@tmp/")
  public final static String TOOL_JDK_PREFIX = "/some/tool/path/jdk/"
  public final static String TOOL_MAVEN_PREFIX = "/some/tool/path/maven/"

  public final static String TOOL_JDK = "sun-java8-jdk"
  public final static String TOOL_MAVEN = "apache-maven3"

  /**
   * Mock for the pipeline DSL object
   */
  protected DSLMock dslMock

  /**
   * Mock for the RunWrapper which provides whitelisted access to the currentBuild
   */
  protected RunWrapperMock runWrapper

  /**
   * Environment variables
   */
  protected EnvActionImplMock envVars

  /**
   * Current build parameters
   */
  protected Map params

  /**
   * Path to the log file
   */
  protected File logFile = null

  /**
   * Utility for recording executed steps
   */
  protected StepRecorder stepRecorder

  @Override
  @Before
  void setUp() throws Exception {
    // add the test folder to the script roots for the BasePipelineTest and call the super function
    scriptRoots += 'test'

    envVars = new EnvActionImplMock()
    envVars.setProperty("PATH", "/usr/bin")
    super.setUp()

    // initialize the RunWrapper Mock
    runWrapper = new RunWrapperMock(mock(Run))

    // initialize the DSL Mock
    this.dslMock = new DSLMock()

    // give the dslMock the refernce to the pipeline helper to allow access to registered libraries
    dslMock.setHelper(helper)

    // initialize the step recorder
    stepRecorder = new StepRecorder()
    StepRecorderAssert.init(stepRecorder)

    // set binding for steps and assign it the the DSL cpsScriptMock
    binding.setVariable("steps", this.dslMock.getMock())

    // set the environment variables
    binding.setVariable('env', envVars)

    // set build parameters
    params = [:]
    binding.setVariable('params', params)

    // set the enviornment variables
    binding.setVariable('params', params)

    // set the currentBuild to the RunWrapper cpsScriptMock
    this.binding.setVariable("currentBuild", runWrapper)

    // add callbacks for DSL functions and pass them to the step recorder if necessary
    helper.registerAllowedMethod("getName", [], canonicalNameCallback)
    helper.registerAllowedMethod("getCanonicalName", [], canonicalNameCallback)
    helper.registerAllowedMethod(ANSI_COLOR, [String.class, Closure.class], ansiColorCallback)
    helper.registerAllowedMethod(ANSIBLE_PLAYBOOK, [Map.class], { Map incomingCall -> stepRecorder.record(ANSIBLE_PLAYBOOK, incomingCall) })

    helper.registerAllowedMethod(BOOLEAN_PARAM, [Map.class], booleanParamCallback)
    helper.registerAllowedMethod(BUILD_DISCARDER, [Object.class], { Map incomingCall -> stepRecorder.record(BUILD_DISCARDER, incomingCall) })

    helper.registerAllowedMethod(CHOICE, [Map.class], choiceCallback)
    helper.registerAllowedMethod(CHECKOUT, [Map.class], { LinkedHashMap incomingCall -> stepRecorder.record(CHECKOUT, incomingCall) })
    helper.registerAllowedMethod(CHECKSTYLE, [LinkedHashMap.class], { LinkedHashMap map -> stepRecorder.record(CHECKSTYLE, map) })
    helper.registerAllowedMethod(CONFIGFILE, [Map.class], configFileCallback)
    helper.registerAllowedMethod(CONFIGFILEPROVIDER, [List.class, Closure.class], configFileProviderCallback)
    helper.registerAllowedMethod(CRON, [String.class], cronCallback)

    helper.registerAllowedMethod(DISABLE_CONCURRENT_BUILDS, [], {
      stepRecorder.record(DISABLE_CONCURRENT_BUILDS, null)
    })

    helper.registerAllowedMethod(FILE_EXISTS, [String.class], fileExistsCallback)

    helper.registerAllowedMethod(EMAILEXT, [Map.class], { Map incomingCall -> stepRecorder.record(EMAILEXT, incomingCall) })
    helper.registerAllowedMethod(ERROR, [String.class], { String incomingCall ->
      stepRecorder.record(ERROR, incomingCall)
      throw new AbortException(incomingCall)
    })
    helper.registerAllowedMethod(FINDBUGS, [LinkedHashMap.class], { LinkedHashMap map -> stepRecorder.record(FINDBUGS, map) })

    helper.registerAllowedMethod(JUNIT, [String.class], { String incomingCall -> stepRecorder.record(JUNIT, incomingCall) })
    helper.registerAllowedMethod(JUNIT, [Map.class], { Map incomingCall -> stepRecorder.record(JUNIT, incomingCall) })

    helper.registerAllowedMethod(LOG_ROTATOR, [Map.class], {
      Map incomingCall ->
        stepRecorder.record(LOG_ROTATOR, incomingCall)
        return [(LOG_ROTATOR): incomingCall]
    })
    helper.registerAllowedMethod(OPENTASKS, [LinkedHashMap.class], { LinkedHashMap map -> stepRecorder.record(OPENTASKS, map) })

    helper.registerAllowedMethod(PARAMETERS, [List.class], { List incomingCall -> stepRecorder.record(PARAMETERS, incomingCall) })
    helper.registerAllowedMethod(PIPELINE_TRIGGERS, [List.class], { List incomingCall -> stepRecorder.record(PIPELINE_TRIGGERS, incomingCall) })
    helper.registerAllowedMethod(PMD, [LinkedHashMap.class], { LinkedHashMap map -> stepRecorder.record(PMD, map) })
    helper.registerAllowedMethod(POLLSCM, [String.class], pollSCMCallback)

    helper.registerAllowedMethod(READ_JSON, [Map.class], readJSONCallback)
    helper.registerAllowedMethod(READ_MAVEN_POM, [Map.class], readMavenPomCallback)
    helper.registerAllowedMethod(READ_YAML, [Map.class], readYamlCallback)

    helper.registerAllowedMethod(SH, [String.class], { String incomingCommand -> stepRecorder.record(SH, incomingCommand) })
    helper.registerAllowedMethod(SH, [Map.class], shellMapCallback)
    helper.registerAllowedMethod(SLEEP, [LinkedHashMap.class], { values -> })
    helper.registerAllowedMethod(SSH_AGENT, [List.class, Closure.class], sshAgentCallback)
    helper.registerAllowedMethod(STAGE, [String.class, Closure.class], stageCallback)
    helper.registerAllowedMethod(STASH, [Map.class], { Map incomingCall -> stepRecorder.record(STASH, incomingCall) })
    helper.registerAllowedMethod(STEP, [Map.class], { LinkedHashMap incomingCall -> stepRecorder.record(STEP, incomingCall) })
    helper.registerAllowedMethod(STRING, [Map.class], stringCallback)

    helper.registerAllowedMethod(TEXT, [Map.class], textCallback)
    helper.registerAllowedMethod(TIMEOUT, [Map.class, Closure.class], timeoutCallback)
    helper.registerAllowedMethod(TIMESTAMPS, [Closure.class], { Closure closure ->
      stepRecorder.record(TIMESTAMPS, true)
      closure.call()
    })
    helper.registerAllowedMethod(TOOL, [String.class], toolCallback)

    helper.registerAllowedMethod(UNSTASH, [Map.class], { Map incomingCall -> stepRecorder.record(UNSTASH, incomingCall) })
    helper.registerAllowedMethod(UPSTREAM, [Map.class], upstreamCallback)

    helper.registerAllowedMethod(VERSIONNUMBER, [LinkedHashMap.class], versionNumberMock)

    // register the current workspace as library
    def projectPath = new File("").getAbsolutePath()
    def library = library().name('local-library')
        .defaultVersion("master")
        .allowOverride(false)
        .implicit(true)
        .targetPath(projectPath)
        .retriever(SelfSourceRetriever.localSourceRetriever(projectPath))
        .build()
    helper.registerSharedLibrary(library)
  }

  def timeoutCallback = {
    Map params, Closure body ->
      stepRecorder.record(TIMEOUT, params)
      body.run()
  }

  def stageCallback = {
    String name, Closure body ->
      stepRecorder.record(STAGE, name)
      body.run()
  }

  def booleanParamCallback = {
    Map config ->
      stepRecorder.record(BOOLEAN_PARAM, config)
      return "booleanParam($config)"
  }

  def choiceCallback = {
    Map config ->
      stepRecorder.record(CHOICE, config)
      return "choice($config)"
  }

  def stringCallback = {
    Map config ->
      stepRecorder.record(STRING, config)
      return "string($config)"
  }

  def textCallback = {
    Map config ->
      stepRecorder.record(TEXT, config)
      return "text($config)"
  }

  /**
   * Callback for pollscm pipeline trigger
   */
  def pollSCMCallback = {
    String config ->
      stepRecorder.record(POLLSCM, config)
      return "pollSCM($config)"
  }

  /**
   * Callback for cron pipeline trigger
   */
  def cronCallback = {
    String config ->
      stepRecorder.record(CRON, config)
      return "cron($config)"
  }

  /**
   * Callback for upstream pipeline trigger
   */
  def upstreamCallback = {
    Map config ->
      stepRecorder.record(UPSTREAM, config)
      return "upstream($config)"
  }

  /**
   * Mocks the 'fileExists' step
   *
   * @return true when file exists, false when file does not exist
   */
  def fileExistsCallback = {
    String path ->
      try {
        File file = this.dslMock.locateTestResource(path)
        return file.exists()
      } catch (AbortException ex) {
        return false
      }
  }

  /**
   * Mocks the 'readYaml' step
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The Maven model
   */
  def readYamlCallback = {
    Map incomingCommand ->
      String file = incomingCommand.file
      String text = incomingCommand.text
      return dslMock.readYaml(file, text)
  }

  /**
   * Mocks the 'readJSON' step
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The file/text as json
   */
  def readJSONCallback = {
    Map incomingCommand ->
      String file = incomingCommand.file
      String text = incomingCommand.text
      return dslMock.readJSON(file, text)
  }

  /**
   * Mocks the 'readMavenPom' step
   * Emulates the readMavenPom step of the Pipeline Utility Steps Plugin
   *
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin">Pipeline Utility Steps Plugin</a>
   *
   * return The Maven model
   */
  def readMavenPomCallback = {
    Map incomingCommand ->
      String path = incomingCommand.file
      File file = this.dslMock.locateTestResource(path)
      InputStream inputStream = new FileInputStream(file)
      Model ret = new MavenXpp3Reader().read(inputStream)
      inputStream.close()
      return ret
  }

  /**
   * Mocks the 'sh' step when executed with named arguments (Map)
   * Used to cpsScriptMock some shell commands executed during integration testing
   *
   * @return A dummy response depending on the incoming command
   */
  def shellMapCallback = { Map incomingCommand ->
    stepRecorder.record(SH, incomingCommand)
    Boolean returnStdout = incomingCommand.returnStdout ?: false
    String script = incomingCommand.script ?: ""
    // return default values for several commands
    if (returnStdout) {
      switch (script) {
        case "git config remote.origin.url": return "http://remote.origin.url/group/project.git"
          break
        case "git rev-parse HEAD": return "0HFGC0"
          break
        case "git branch": return "* (detached from 0HFGC0)"
          break
        default: return ""
      }
    }
  }

  /**
   * Callback to allow getting the canonical name of the calling function when used inside classes
   * by using the Stacktrace
   * // TODO find better solution
   *
   * @return the Canoncial name of the object
   */
  def canonicalNameCallback = { closure ->
    Throwable t = new Throwable()
    Pattern pattern = Pattern.compile('^[a-z]+[^.]*$')
    String foundClassName = ""
    t.getStackTrace().each { StackTraceElement item ->
      if (item.getClassName().matches(pattern)) {
        foundClassName = item.getClassName()
        return foundClassName
      }
    }
    return foundClassName
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

  /**
   * Mocks the 'sshagent' step
   */
  def sshAgentCallback = { List list, Closure closure ->
    stepRecorder.record(SSH_AGENT, list)
    closure.run()
  }

  /**
   * Mocks the 'versionNumber' step
   *
   * @return The formatted versionNumber number
   */
  def versionNumberMock = { Map map ->
    stepRecorder.record(VERSIONNUMBER, map)
    String projectStartDate = map.projectStartDate ?: "1970-01-01"
    String versionNumberString = map.versionNumberString ?: ""
    VersionNumberStep versionNumberStep = new VersionNumberStep(versionNumberString)
    versionNumberStep.projectStartDate = projectStartDate
    VersionNumberBuildInfo versionNumberBuildInfo = new VersionNumberBuildInfo(0, 0, 0, 0, 0)
    Calendar timeStamp = Calendar.getInstance()
    String result = VersionNumberCommon.formatVersionNumber(versionNumberString, versionNumberStep.getProjectStartDate(), versionNumberBuildInfo, this.envVars.getEnvironment(), timeStamp)
    return result
  }

  /**
   * Returns the value of an environment variable
   *
   * @param var The name of the environment variable to return
   * @return The value of the environment variable
   */
  protected getEnv(String var) {
    return this.envVars.getProperty(var)
  }

  /**
   * Sets an environment variable
   *
   * @param var The name of the environment variable
   * @param value The value of the environment variable
   */
  protected setEnv(String var, String value) {
    this.envVars.setProperty(var, value)
  }

  /**
   * Utility function to load and execute a script (e.g. test pipeline)
   * The function calls the utility function 'beforeLoadingScript' before loading the script via the JenkinsPipelineUnit
   * framework. To enable the test to redirect executed steps defined in the implicitely loaded library the function
   * 'afterLoadingScript' is executed afterwards
   *
   * @param scriptPath The Path to the test job
   * @return The return value of the executed script
   */
  protected loadAndExecuteScript(String scriptPath) {
    def ret
    try {
      // call helper function to enable tests to execute code before loading the script
      beforeLoadingScript()
      def script = loadScript(scriptPath)
      // call helper function to enable tests to redirect pipeline steps into own callbacks
      afterLoadingScript()
      ret = script.execute()
    } catch (e) {
      e.printStackTrace()
      dslMock.printLogMessages()
      throw e
    }
    return ret
  }

  /**
   * Utility function to enable tests to put code in front of the loading of the script
   */
  protected void beforeLoadingScript() {}

  /**
   * Utility function to enable tests to execute code after loading but before executing the script
   * This is especially helpful in cases were you want to redirect/cpsScriptMock steps defined in the library to own callbacks to
   * analyze call parameters.
   */
  protected void afterLoadingScript() {}

  /**
   * Mocks the 'configFileProvider' step. For each ManagedFile the environment variable is set to a dummy filepath
   */
  def configFileProviderCallback = { List<ManagedFile> configFiles, Closure closure ->
    stepRecorder.record(CONFIGFILEPROVIDER, configFiles)
    configFiles.each { ManagedFile file ->
      String filePath = file.getTargetLocation()
      if (filePath == null || filePath.isEmpty()) {
        filePath = WORKSPACE_TMP_PATH.concat(file.fileId)
      }
      file.setTargetLocation(filePath)
      if (file.getVariable() != null && file.getVariable().length() > 0) {
        Exception catchedException = null
        try {
          if (getEnv(file.getVariable()) != null) {
            throw new Exception("${file.getVariable()} is already registered!")
          }
        } catch (Exception e) {
          catchedException = e
        }
        Assert.assertNull("The config provider already has a configFile with variable " + file.getVariable(), catchedException)
        setEnv(file.getVariable(), filePath)
      }
    }
    closure.run()
  }

  /**
   * Mocks the 'configFileProvider' step. For each ManagedFile the environment variable is set to a dummy filepath
   */
  def ansiColorCallback = {
    String colorMode, Closure body ->
      stepRecorder.record(ANSI_COLOR, colorMode)
      this.setEnv('TERM', colorMode)
      body.run()
      this.setEnv('TERM', null)
  }

  /**
   * Mocks the 'tool' step
   */
  def toolCallback = { String tool ->
    stepRecorder.record(TOOL, tool)
    switch (tool) {
      case TOOL_MAVEN:
        return TOOL_MAVEN_PREFIX.concat(tool)
      case TOOL_JDK:
        return TOOL_JDK_PREFIX.concat(tool)
    }
    return ""
  }

  /**
   * @return The current build parameters
   */
  Map getParams() {
    return params
  }

  /**
   * Sets the current build parameters
   *
   * @param params
   */
  void setParams(Map params) {
    this.params = params
    this.binding.setVariable("params", params)
  }
}
