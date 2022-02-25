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
 * Constants for steps
 */
class StepConstants {

  public final static String ADD_BADGE = "addBadge"
  public final static String ADD_ERROR_BADGE = "addErrorBadge"
  public final static String ADD_HTML_BADGE = "addHtmlBadge"
  public final static String ADD_INFO_BADGE = "addInfoBadge"
  public final static String ADD_SHORT_TEXT = "addShortText"
  public final static String ADD_WARNING_BADGE = "addWarningBadge"

  public final static String ANALYSISPUBLISHER = "AnalysisPublisher"
  public final static String ANSI_COLOR = "ansiColor"
  public final static String ANSIBLE_PLAYBOOK = "ansiblePlaybook"

  public final static String BOOLEAN_PARAM = "booleanParam"
  public final static String BUILD_DISCARDER = "buildDiscarder"
  public final static String BUILD_BLOCKER_PROPERTY = "BuildBlockerProperty"

  public final static String CHECKOUT = "checkout"
  public final static String CHECKSTYLE = "checkstyle"
  public final static String CHECK_STYLE = "checkStyle"
  public final static String CHECKOUT_SCM = "checkoutScm"
  public final static String CHOICE = "choice"
  public final static String CONFIGFILE = "configFile"
  public final static String CONFIGFILEPROVIDER = "configFileProvider"
  public final static String CREATE_SUMMARY = "createSummary"
  public final static String CRON = "cron"


  public final static String DISABLE_CONCURRENT_BUILDS = "disableConcurrentBuilds"
  public final static String DIR = "dir"

  public final static String EMAILEXT = "emailext"
  public final static String ENV = "env"
  public final static String ENV_SET_PROPERTY = "env.setProperty"
  public final static String ENV_GET_PROPERTY = "env.getProperty"
  public final static String ERROR = "error"

  public final static String FILE_EXISTS = "fileExists"
  public final static String FIND_FILES = "findFiles"
  public final static String FINDBUGS = "findbugs"
  public final static String FIND_BUGS = "findBugs"
  public final static String FINGERPRINT = "fingerprint"

  public final static String JACOCOPUBLISHER = "JacocoPublisher"
  public final static String JUNIT = "junit"
  public final static String JUNIT_PARSER = "junitParser"

  public final static String HTTP_REQUEST = "httpRequest"

  public final static String LOG_ROTATOR = "logRotator"

  public final static String MANAGED_SCRIPTS_EXEC_JENKINS_SHELL_SCRIPT = "execJenkinsShellScript"
  public final static String MANAGED_SCRIPTS_EXEC_PIPELINE_SHELL_SCRIPT = "execPipelineShellScript"

  public final static String MATTERMOST_SEND = "mattermostSend"

  public final static String MAVEN_PURGE_SNAPSHOTS = "purgeSnapshots"

  public final static String MQTT_NOTIFICATION = "mqttNotification"

  public final static String NODE = "node"

  public final static String OPENTASKS = "openTasks"

  public final static String PARAMETERS = "parameters"
  public final static String PIPELINE_TRIGGERS = "pipelineTriggers"
  public final static String PMD = "pmd"
  public final static String PMD_PARSER = "pmdParser"
  public final static String POLLSCM = "pollSCM"
  public final static String PROPERTIES = "properties"


  public final static String READ_JSON = "readJSON"
  public final static String READ_MAVEN_POM = "readMavenPom"
  public final static String READ_YAML = "readYaml"
  public final static String RECORD_ISSUES = "recordIssues"

  public final static String RETRY = "retry"

  public final static String REMOVE_BADGES = "removeBadges"
  public final static String REMOVE_HTML_BADGES = "removeHtmlBadges"

  public final static String SET_BUILD_NAME = "setBuildName"
  public final static String SH = "sh"
  public final static String SLEEP = "sleep"
  public final static String SSH_AGENT = "sshagent"
  public final static String STAGE = "stage"
  public final static String STASH = "stash"
  public final static String STEP = "step"
  public final static String STRING = "string"

  public final static String OFFICE365_CONNECTOR_SEND = "office365ConnectorSend"

  public final static String TASK_SCANNER = "taskScanner"
  public final static String TEXT = "text"
  public final static String TIMESTAMPS = "timestamps"
  public final static String TIMEOUT = "timeout"
  public final static String TOOL = "tool"

  public final static String UNSTASH = "unstash"
  public final static String UPSTREAM = "upstream"
  public final static String USERNAME_PASSWORD = "usernamePassword"

  public final static String VERSIONNUMBER = "VersionNumber"

  public final static String WRITE_FILE = "writeFile"
  public final static String WITH_CREDENTIALS = "withCredentials"
  public final static String WITH_ENV = "withEnv"

  public final static String XUNITBUILDER = "XUnitBuilder"


}
