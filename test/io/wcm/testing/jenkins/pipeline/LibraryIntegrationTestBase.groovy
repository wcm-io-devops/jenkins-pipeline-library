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
import io.wcm.devops.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.testing.jenkins.pipeline.global.lib.SelfSourceRetriever
import io.wcm.testing.jenkins.pipeline.plugins.*
import io.wcm.testing.jenkins.pipeline.plugins.credentials.CredentialsPluginMock
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorder
import org.junit.Before

import java.util.regex.Pattern

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library

/**
 * Base class for integration tests that use the JenkinsPipelineUnit testing framework
 *
 * @see <a href="https://github.com/lesfurets/JenkinsPipelineUnit">JenkinsPipelineUnit</a>
 */
class LibraryIntegrationTestBase extends BasePipelineTest {

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
   *  Mocks the Core steps
   */
  CoreStepsMock coreStepsMock

  /**
   * Mocks the pipeline utility steps plugin
   */
  PipelineUtilityStepsPluginMock pipelineUtilityStepsPluginMock

  /**
   * Mocks the AnsiColor plugin
   */
  AnsiColorPluginMock ansiColorPluginMock

  /**
   * Mocks the Ansible plugin
   */
  AnsiblePluginMock ansiblePluginMock

  /**
   * @deprecated please use context.getStepRecorder() instead
   *
   * Reference to the StepRecorder instance
   */
  StepRecorder stepRecorder

  /**
   * @deprecated please use context.getRunWrapperMock() instead
   *
   * Reference to the runWrapper
   */
  RunWrapperMock runWrapper

  /**
   * Mocks the Checkstyle plugin
   */
  CheckstylePluginMock checkstylePluginMock

  /**
   * Mocks the Email-ext plugin
   */
  EmailExtPluginMock emailExtPluginMock

  /**
   * Mocks the FindBugs plugin
   */
  FindBugsPluginMock findBugsPluginMock

  /**
   * Mocks the JUnit olugin
   */
  JUnitPluginMock jUnitPluginMock

  /**
   * Mocks the Task Scanner plugin
   */
  TaskScannerPluginMock taskScannerPluginMock

  /**
   * Mocks the SSH Agent plugin
   */
  SSHAgentPluginMock sshAgentPluginMock

  /**
   * Mocks the PMD plugin
   */
  PMDPluginMock pmdPluginMock

  /**
   * Mocks the Timestamper plugin
   */
  TimestamperPluginMock timestamperPluginMock

  /**
   * Mocks the Version Number plugin
   */
  VersionNumberPluginMock versionNumberPluginMock

  /**
   * Mocks the Pipeline Stage Step plugin
   */
  PipelineStageStepPluginMock pipelineStageStepPluginMock

  /**
   * Mocks the workflow-durable-task-step plugin
   */
  WorkflowDurableTaskStepPluginMock workflowDurableTaskStepPluginMock

  /**
   * Mocks the http_request plugin
   */
  HTTPRequestPluginMock httpRequestPluginMock

  /**
   * Mocks the MQTT notification plugin
   */
  MQTTNotificationPluginMock mqttNotificationPluginMock

  /**
   * Mocks the Mattermost notification plugin
   */
  MattermostNotificationPluginMock mattermostNotificationPluginMock

  /**
   * Mocks the Office365 connector plugin for MS Teams notifications
   */
  TeamsNotificationPluginMock teamsNotificationPluginMock

  @Override
  @Before
  void setUp() throws Exception {
    // add the test folder to the script roots for the BasePipelineTest and call the super function
    scriptRoots += 'test'

    super.setUp()

    context = new LibraryIntegrationTestContext(helper, binding)

    this.stepRecorder = context.getStepRecorder()
    this.runWrapper = context.getRunWrapperMock()

    // add badge plugin mocks
    this.ansiblePluginMock = new AnsiblePluginMock(context)
    this.ansiColorPluginMock = new AnsiColorPluginMock(context)
    this.basicStepsMock = new BasicStepsMock(context)
    this.badgePluginMock = new BadgePluginMock(context)
    this.checkstylePluginMock = new CheckstylePluginMock(context)
    this.configFileProviderPluginMock = new ConfigFileProviderPluginMock(context)
    this.coreStepsMock = new CoreStepsMock(context)
    this.credentialsPluginMock = new CredentialsPluginMock(context)
    this.emailExtPluginMock = new EmailExtPluginMock(context)
    this.findBugsPluginMock = new FindBugsPluginMock(context)
    this.jobPropertiesMock = new JobPropertiesMock(context)
    this.jUnitPluginMock = new JUnitPluginMock(context)
    this.pipelineStageStepPluginMock = new PipelineStageStepPluginMock(context)
    this.pipelineUtilityStepsPluginMock = new PipelineUtilityStepsPluginMock(context)
    this.pmdPluginMock = new PMDPluginMock(context)
    this.sshAgentPluginMock = new SSHAgentPluginMock(context)
    this.taskScannerPluginMock = new TaskScannerPluginMock(context)
    this.timestamperPluginMock = new TimestamperPluginMock(context)
    this.versionNumberPluginMock = new VersionNumberPluginMock(context)
    this.workflowDurableTaskStepPluginMock = new WorkflowDurableTaskStepPluginMock(context)
    this.httpRequestPluginMock = new HTTPRequestPluginMock(context)
    this.mqttNotificationPluginMock = new MQTTNotificationPluginMock(context)
    this.mattermostNotificationPluginMock = new MattermostNotificationPluginMock(context)
    this.teamsNotificationPluginMock = new TeamsNotificationPluginMock(context)

    context.getPipelineTestHelper().registerAllowedMethod("getName", [], canonicalNameCallback)
    context.getPipelineTestHelper().registerAllowedMethod("getCanonicalName", [], canonicalNameCallback)

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

    this.setEnv("BUILD_NUMBER", "2")
    this.setEnv("JOB_NAME", "MOCKED_JOB_NAME")
    this.setEnv("BUILD_URL", "MOCKED_BUILD_URL")
    this.setEnv("JOB_BASE_NAME", "MOCKED%2FJOB_BASE_NAME")
    this.setEnv("GIT_BRANCH", "MOCKED_GIT_BRANCH")
  }

  /**
   * Callback to allow getting the canonical name of the calling function when used inside classes
   * by using the Stacktrace
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
   * Returns the value of an environment variable
   *
   * @param var The name of the environment variable to return
   * @return The value of the environment variable
   */
  protected getEnv(String var) {
    return this.context.getEnvVars().getProperty(var)
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
   * @param config This map will be passed to the execute function of the script
   * @return The return value of the executed script
   */
  protected loadAndExecuteScript(String scriptPath, Map config = null) {
    def ret
    try {
      // call helper function to enable tests to execute code before loading the script
      beforeLoadingScript()
      def script = loadScript(scriptPath)
      Logger.initialized = false
      Logger.init((groovy.lang.Script) script, LogLevel.INFO)
      // call helper function to enable tests to redirect pipeline steps into own callbacks
      afterLoadingScript()
      if (config != null) {
        ret = script.execute(config)
      } else {
        ret = script.execute()
      }
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


}
