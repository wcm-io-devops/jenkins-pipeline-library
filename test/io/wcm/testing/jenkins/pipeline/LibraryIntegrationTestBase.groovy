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
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.testing.jenkins.pipeline.global.lib.SelfSourceRetriever
import io.wcm.testing.jenkins.pipeline.plugins.BadgePluginMock
import io.wcm.testing.jenkins.pipeline.plugins.ConfigFileProviderPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.PipelineUtilityStepsPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.credentials.CredentialsPluginMock
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.apache.tools.ant.DirectoryScanner
import org.jenkinsci.plugins.pipeline.utility.steps.fs.FileWrapper
import org.junit.Before
import org.jvnet.hudson.tools.versionnumber.VersionNumberBuildInfo
import org.jvnet.hudson.tools.versionnumber.VersionNumberCommon
import org.jvnet.hudson.tools.versionnumber.VersionNumberStep

import java.nio.file.Path
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
   * Path to the log file
   */
  protected File logFile = null

  /**
   * Context for IT Tests
   */
  LibraryIntegrationTestContext context

  /**
   * Mocks for badge plugin
   */
  BadgePluginMock badgePluginMock

  /**
   * Mocks for credentials plugin
   */
  CredentialsPluginMock credentialsPluginMock

  /**
   * Mocks for basic steps
   */
  BasicStepsMock basicStepsMock

  /**
   * Mock for build parameters
   */
  JobPropertiesMock jobPropertiesMock

  /**
   *  Mocks the Config File Provider plugin
   */
  ConfigFileProviderPluginMock configFileProviderPluginMock

  /**
   * Mocks the pipeline utility steps plugin
   */
  PipelineUtilityStepsPluginMock pipelineUtilityStepsPluginMock

  @Override
  @Before
  void setUp() throws Exception {
    // add the test folder to the script roots for the BasePipelineTest and call the super function
    scriptRoots += 'test'


    super.setUp()



    context = new LibraryIntegrationTestContext(helper, binding)

    // add badge plugin mocks
    this.badgePluginMock = new BadgePluginMock(context)

    // add credentials plugin mocks
    this.credentialsPluginMock = new CredentialsPluginMock(context)

    // add basic step mocks
    this.basicStepsMock = new BasicStepsMock(context)

    // add config file provider plugin mock
    this.configFileProviderPluginMock = new ConfigFileProviderPluginMock(context)

    // add job properties mock
    this.jobPropertiesMock = new JobPropertiesMock(context)

    // add pipeline utility steps plugin mock
    this.pipelineUtilityStepsPluginMock = new PipelineUtilityStepsPluginMock(context)

    // add callbacks for DSL functions and pass them to the step recorder if necessary
    context.getPipelineTestHelper().registerAllowedMethod(ANSI_COLOR, [String.class, Closure.class], ansiColorCallback)
    context.getPipelineTestHelper().registerAllowedMethod(ANSIBLE_PLAYBOOK, [Map.class], { Map incomingCall -> context.getStepRecorder().record(ANSIBLE_PLAYBOOK, incomingCall) })

    context.getPipelineTestHelper().registerAllowedMethod(CHECKSTYLE, [LinkedHashMap.class], { LinkedHashMap map -> context.getStepRecorder().record(CHECKSTYLE, map) })

    context.getPipelineTestHelper().registerAllowedMethod(DIR, [String.class, Closure.class], dirCallback)

    context.getPipelineTestHelper().registerAllowedMethod(EMAILEXT, [Map.class], { Map incomingCall -> context.getStepRecorder().record(EMAILEXT, incomingCall) })
    context.getPipelineTestHelper().registerAllowedMethod(ERROR, [String.class], { String incomingCall ->
      context.getStepRecorder().record(ERROR, incomingCall)
      throw new AbortException(incomingCall)
    })
    context.getPipelineTestHelper().registerAllowedMethod(FILE_EXISTS, [String.class], fileExistsCallback)

    context.getPipelineTestHelper().registerAllowedMethod(FINDBUGS, [LinkedHashMap.class], { LinkedHashMap map -> context.getStepRecorder().record(FINDBUGS, map) })

    context.getPipelineTestHelper().registerAllowedMethod("getName", [], canonicalNameCallback)
    context.getPipelineTestHelper().registerAllowedMethod("getCanonicalName", [], canonicalNameCallback)

    context.getPipelineTestHelper().registerAllowedMethod(JUNIT, [String.class], { String incomingCall -> context.getStepRecorder().record(JUNIT, incomingCall) })
    context.getPipelineTestHelper().registerAllowedMethod(JUNIT, [Map.class], { Map incomingCall -> context.getStepRecorder().record(JUNIT, incomingCall) })


    context.getPipelineTestHelper().registerAllowedMethod(OPENTASKS, [LinkedHashMap.class], { LinkedHashMap map -> context.getStepRecorder().record(OPENTASKS, map) })

    context.getPipelineTestHelper().registerAllowedMethod(PMD, [LinkedHashMap.class], { LinkedHashMap map -> context.getStepRecorder().record(PMD, map) })

    context.getPipelineTestHelper().registerAllowedMethod(SH, [String.class], { String incomingCommand -> context.getStepRecorder().record(SH, incomingCommand) })
    context.getPipelineTestHelper().registerAllowedMethod(SH, [Map.class], shellMapCallback)
    context.getPipelineTestHelper().registerAllowedMethod(SLEEP, [LinkedHashMap.class], { values -> })
    context.getPipelineTestHelper().registerAllowedMethod(SSH_AGENT, [List.class, Closure.class], sshAgentCallback)
    context.getPipelineTestHelper().registerAllowedMethod(STAGE, [String.class, Closure.class], stageCallback)
    context.getPipelineTestHelper().registerAllowedMethod(STASH, [Map.class], { Map incomingCall -> context.getStepRecorder().record(STASH, incomingCall) })
    context.getPipelineTestHelper().registerAllowedMethod(STEP, [Map.class], { LinkedHashMap incomingCall -> context.getStepRecorder().record(STEP, incomingCall) })

    context.getPipelineTestHelper().registerAllowedMethod(TIMEOUT, [Map.class, Closure.class], timeoutCallback)
    context.getPipelineTestHelper().registerAllowedMethod(TIMESTAMPS, [Closure.class], { Closure closure ->
      context.getStepRecorder().record(TIMESTAMPS, true)
      closure.call()
    })
    context.getPipelineTestHelper().registerAllowedMethod(TOOL, [String.class], toolCallback)

    context.getPipelineTestHelper().registerAllowedMethod(UNSTASH, [Map.class], { Map incomingCall -> context.getStepRecorder().record(UNSTASH, incomingCall) })

    context.getPipelineTestHelper().registerAllowedMethod(VERSIONNUMBER, [LinkedHashMap.class], versionNumberMock)

    context.getPipelineTestHelper().registerAllowedMethod(WRITE_FILE, [Map.class], { Map incomingCall -> context.getStepRecorder().record(WRITE_FILE, incomingCall) })

    // register the current workspace as library
    def projectPath = new File("").getAbsolutePath()
    def library = library().name('local-library')
      .defaultVersion("master")
      .allowOverride(false)
      .implicit(true)
      .targetPath(projectPath)
      .retriever(SelfSourceRetriever.localSourceRetriever(projectPath))
      .build()
    context.getPipelineTestHelper().registerSharedLibrary(library)
  }

  /**
   * Callback for timeout step
   */
  def timeoutCallback = {
    Map params, Closure body ->
      context.getStepRecorder().record(TIMEOUT, params)
      body.run()
  }

  /**
   * Callback for dir step
   */
  def dirCallback = {
    String dir, Closure body ->
      context.getStepRecorder().record(DIR, dir)
      body.run()
  }

  /**
   * Callback for stage step
   */
  def stageCallback = {
    String name, Closure body ->
      context.getStepRecorder().record(STAGE, name)
      body.run()
  }

  /**
   * Mocks the 'fileExists' step
   *
   * @return true when file exists, false when file does not exist
   */
  def fileExistsCallback = {
    String path ->
      try {
        File file = this.context.getDslMock().locateTestResource(path)
        return file.exists()
      } catch (AbortException ex) {
        return false
      }
  }

  /**
   * Mocks the 'sh' step when executed with named arguments (Map)
   * Used to cpsScriptMock some shell commands executed during integration testing
   *
   * @return A dummy response depending on the incoming command
   */
  def shellMapCallback = { Map incomingCommand ->
    context.getStepRecorder().record(SH, incomingCommand)
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
   * Mocks the 'sshagent' step
   */
  def sshAgentCallback = { List list, Closure closure ->
    context.getStepRecorder().record(SSH_AGENT, list)
    closure.run()
  }

  /**
   * Mocks the 'versionNumber' step
   *
   * @return The formatted versionNumber number
   */
  def versionNumberMock = { Map map ->
    context.getStepRecorder().record(VERSIONNUMBER, map)
    String projectStartDate = map.projectStartDate ?: "1970-01-01"
    String versionNumberString = map.versionNumberString ?: ""
    VersionNumberStep versionNumberStep = new VersionNumberStep(versionNumberString)
    versionNumberStep.projectStartDate = projectStartDate
    VersionNumberBuildInfo versionNumberBuildInfo = new VersionNumberBuildInfo(0, 0, 0, 0, 0)
    Calendar timeStamp = Calendar.getInstance()
    String result = VersionNumberCommon.formatVersionNumber(versionNumberString, versionNumberStep.getProjectStartDate(), versionNumberBuildInfo, this.context.getEnvVars().getEnvironment(), timeStamp)
    return result
  }

  /**
   * Returns the value of an environment variable
   *
   * @param var The name of the environment variable to return
   * @return The value of the environment variable
   */
  protected getEnv(String var) {
    return this.context.getEnvVars().getProperty(var)
  }

  /**
   * Utility function to get an argument from dynamic arguments
   *
   * @param args The object containing the arguments
   * @param index The index of the argument that should be parsed
   * @param defaultValue
   * @return The found arg or defaultValue when arg is not present
   */
  protected Object getArgAt(Object args, Integer index, defaultValue = null) {
    return (args.length > index ? args.getAt(index) : null)
  }

  /**
   * Sets an environment variable
   *
   * @param var The name of the environment variable
   * @param value The value of the environment variable
   */
  protected setEnv(String var, String value) {
    this.context.getEnvVars().setProperty(var, value)
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
      this.context.getDslMock().printLogMessages()
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
  def ansiColorCallback = {
    String colorMode, Closure body ->
      context.getStepRecorder().record(ANSI_COLOR, colorMode)
      this.setEnv('TERM', colorMode)
      body.run()
      this.setEnv('TERM', null)
  }

  /**
   * Mocks the 'tool' step
   */
  def toolCallback = { String tool ->
    context.getStepRecorder().record(TOOL, tool)
    switch (tool) {
      case TOOL_MAVEN:
        return TOOL_MAVEN_PREFIX.concat(tool)
      case TOOL_JDK:
        return TOOL_JDK_PREFIX.concat(tool)
    }
    return ""
  }
}
