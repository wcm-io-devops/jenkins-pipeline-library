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
import io.wcm.testing.jenkins.pipeline.plugins.AnsiColorPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.AnsiblePluginMock
import io.wcm.testing.jenkins.pipeline.plugins.BadgePluginMock
import io.wcm.testing.jenkins.pipeline.plugins.CheckstylePluginMock
import io.wcm.testing.jenkins.pipeline.plugins.ConfigFileProviderPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.EmailExtPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.FindBugsPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.JUnitPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.PMDPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.PipelineStageStepPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.PipelineUtilityStepsPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.SSHAgentPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.TaskScannerPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.TimestamperPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.VersionNumberPluginMock
import io.wcm.testing.jenkins.pipeline.plugins.WorkflowDurableTaskStepPluginMock
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

  /**
   * @deprecated use LibraryIntegrationTestContext.WORKSPACE_PATH instead
   */
  public final static String WORKSPACE_PATH = LibraryIntegrationTestContext.WORKSPACE_PATH

  /**
   * @deprecated use LibraryIntegrationTestContext.WORKSPACE_TMP_PATH instead
   */
  public final static String WORKSPACE_TMP_PATH = LibraryIntegrationTestContext.WORKSPACE_TMP_PATH

  /**
   * @deprecated use LibraryIntegrationTestContext.TOOL_JDK_PREFIX instead
   */
  public final static String TOOL_JDK_PREFIX = LibraryIntegrationTestContext.TOOL_JDK_PREFIX
  /**
   * @deprecated use LibraryIntegrationTestContext.TOOL_MAVEN_PREFIX instead
   */
  public final static String TOOL_MAVEN_PREFIX = LibraryIntegrationTestContext.TOOL_MAVEN_PREFIX

  /**
   * @deprecated use LibraryIntegrationTestContext.TOOL_JDK instead
   */
  public final static String TOOL_JDK = LibraryIntegrationTestContext.TOOL_JDK
  /**
   * @deprecated use LibraryIntegrationTestContext.TOOL_MAVEN instead
   */
  public final static String TOOL_MAVEN = LibraryIntegrationTestContext.TOOL_MAVEN

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


}
