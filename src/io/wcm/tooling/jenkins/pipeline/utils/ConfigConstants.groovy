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
package io.wcm.tooling.jenkins.pipeline.utils

/**
 * Constants for configuration values. Used for passing configuration options into the library steps
 */
class ConfigConstants {

  public static final ANSI_COLOR = "ansiColor"
  public static final ANSI_COLOR_XTERM = "xterm"
  public static final ANSI_COLOR_GNOME_TERMINAL = "gnome-terminal"
  public static final ANSI_COLOR_VGA = "vga"
  public static final ANSI_COLOR_CSS = "css"

  public static final String ANSIBLE = "ansible"
  public static final String ANSIBLE_COLORIZED = "colorized"
  public static final String ANSIBLE_CREDENTIALS_ID = "credentialsId"
  public static final String ANSIBLE_EXTRA_PARAMETERS = "extraParameters"
  public static final String ANSIBLE_EXTRA_VARS = "extraVars"
  public static final String ANSIBLE_FORKS = "forks"
  public static final String ANSIBLE_INJECT_PARAMS = "injectParams"
  public static final String ANSIBLE_INSTALLATION = "installation"
  public static final String ANSIBLE_INVENTORY = "inventory"
  public static final String ANSIBLE_LIMIT = "limit"
  public static final String ANSIBLE_SKIPPED_TAGS = "skippedTags"
  public static final String ANSIBLE_START_AT_TASK = "startAtTask"
  public static final String ANSIBLE_TAGS = "tags"
  public static final String ANSIBLE_SUDO = "sudo"
  public static final String ANSIBLE_SUDO_USER = "sudoUser"
  public static final String ANSIBLE_PLAYBOOK = "playbook"

  public static final String MAVEN = "maven"
  public static final String MAVEN_ARGUMENTS = "arguments"
  public static final String MAVEN_DEFINES = "defines"
  public static final String MAVEN_EXECUTABLE = "executable"
  public static final String MAVEN_GLOBAL_SETTINGS = "globalSettings"
  public static final String MAVEN_GOALS = "goals"
  public static final String MAVEN_INJECT_PARAMS = "injectParams"
  public static final String MAVEN_POM = "pom"
  public static final String MAVEN_PROFILES = "profiles"
  public static final String MAVEN_SETTINGS = "settings"

  public static final String LOGLEVEL = "logLevel"

  public static final String NOTIFY = "notify"
  public static final String NOTIFY_ATTACH_LOG = "attachLog"
  public static final String NOTIFY_ATTACHMENTS_PATTERN = "attachmentsPattern"
  public static final String NOTIFY_BODY = "body"
  public static final String NOTIFY_COMPRESS_LOG = "compressLog"
  public static final String NOTIFY_MIME_TYPE = "mimeType"
  public static final String NOTIFY_ON_SUCCESS = "onSuccess"
  public static final String NOTIFY_ON_FAILURE = "onFailure"
  public static final String NOTIFY_ON_STILL_FAILING = "onStillFailing"
  public static final String NOTIFY_ON_FIXED = "onFixed"
  public static final String NOTIFY_ON_UNSTABLE = "onUnstable"
  public static final String NOTIFY_ON_STILL_UNSTABLE = "onStillUnstable"
  public static final String NOTIFY_ON_ABORT = "onAbort"
  public static final String NOTIFY_RECIPIENT_PROVIDERS = "recipientProviders"
  public static final String NOTIFY_SUBJECT = "subject"
  public static final String NOTIFY_TO = "to"

  public static final String NPM = "NPM"
  public static final String NPM_ARGUMENTS = "arguments"
  public static final String NPM_EXECUTABLE = "executable"

  public static final String SCM = "scm"
  public static final String SCM_BRANCHES = "branches"
  public static final String SCM_CREDENTIALS_ID = "credentialsId"
  public static final String SCM_DO_GENERATE_SUBMODULE_CONFIGURATION = "doGenerateSubmoduleConfigurations"
  public static final String SCM_EXTENSIONS = "extensions"
  public static final String SCM_SUBMODULE_CONFIG = "submoduleCfg"
  public static final String SCM_URL = "url"
  public static final String SCM_USER_REMOTE_CONFIG = "userRemoteConfig"
  public static final String SCM_USER_REMOTE_CONFIGS = "userRemoteConfigs"
  public static final String SCM_USE_SCM_VAR = "useScmVar"

  public static final String SCP = "scp"
  public static final String SCP_ARGUMENTS = "arguments"
  public static final String SCP_DESTINATION = "destination"
  public static final String SCP_EXECUTABLE = "executable"
  public static final String SCP_HOST = "host"
  public static final String SCP_HOST_KEY_CHECK = "hostKeyCheck"
  public static final String SCP_PORT = "port"
  public static final String SCP_RECURSIVE = "recursive"
  public static final String SCP_SOURCE = "source"
  public static final String SCP_USER = "user"

  public static final String TOOLS = "tools"
  public static final String TOOL_ENVVAR = "envVar"
  public static final String TOOL_NAME = "name"
  public static final String TOOL_TYPE = "type"


}
