/**
 * Integration tests for pipeline to determine sandbox/cps problems in pipeline classes after jenkins update
 * To detect problems caused by plugin updates in most cases the instance creation of a class is sufficient to cause a
 * cps/sandbox exception.
 */
@Library('pipeline-library') pipelineLibrary
library identifier: 'pipeline-library-example@master', retriever: modernSCM([
  $class: 'GitSCMSource',
  remote: 'https://github.com/wcm-io-devops/jenkins-pipeline-library-example.git'
])

import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.credentials.CredentialConstants
import io.wcm.devops.jenkins.pipeline.credentials.CredentialParser
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFile
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFileConstants
import io.wcm.devops.jenkins.pipeline.managedfiles.ManagedFileParser
import io.wcm.devops.jenkins.pipeline.model.PatternMatchable
import io.wcm.devops.jenkins.pipeline.model.Result
import io.wcm.devops.jenkins.pipeline.model.Tool
import io.wcm.devops.jenkins.pipeline.scm.GitRepository
import io.wcm.devops.jenkins.pipeline.shell.CommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.shell.GitCommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.shell.MavenCommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.shell.ScpCommandBuilderImpl
import io.wcm.devops.jenkins.pipeline.shell.ShellUtils
import io.wcm.devops.jenkins.pipeline.tools.ansible.Role
import io.wcm.devops.jenkins.pipeline.tools.ansible.RoleRequirements
import io.wcm.devops.jenkins.pipeline.utils.IntegrationTestHelper
import io.wcm.devops.jenkins.pipeline.utils.ListUtils
import io.wcm.devops.jenkins.pipeline.utils.NotificationTriggerHelper
import io.wcm.devops.jenkins.pipeline.utils.PatternMatcher
import io.wcm.devops.jenkins.pipeline.utils.TypeUtils
import io.wcm.devops.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.maps.MapUtils
import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import io.wcm.devops.jenkins.pipeline.utils.resources.LibraryResource
import io.wcm.devops.jenkins.pipeline.versioning.ComparableVersion
import io.wcm.devops.jenkins.pipeline.versioning.IntegerItem
import io.wcm.devops.jenkins.pipeline.versioning.ListItem
import io.wcm.devops.jenkins.pipeline.versioning.StringItem
import net.sf.json.JSON
import org.apache.maven.model.Plugin
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import io.wcm.devops.jenkins.pipeline.utils.ConfigConstants

// job properties

properties([
  disableConcurrentBuilds(),
  pipelineTriggers([pollSCM('H * * * * ')])
])

Logger.init(this, LogLevel.INFO)
Logger log = new Logger(this)

node() {
  checkout scm

  ComparableVersion minimalReleasePluginVersion = new ComparableVersion("2.5.3")

  // read the maven pom
  def mavenModel = readMavenPom(file: 'jenkinsfiles/effective-pom.xml')
  Map<String, Plugin> map = mavenModel.getBuild().getPluginManagement().getPluginsAsMap()

  def mavenReleasePlugin = map.get("org.apache.maven.plugins:maven-release-plugin")
  if (!mavenReleasePlugin) {
    error("No maven deploy plugin found in effective pom!")
  }
  String version = mavenReleasePlugin.getVersion()
  log.debug("mavenReleasePlugin version: "+version)

  ComparableVersion actualReleasePluginVersion = new ComparableVersion(version.toString())
  log.debug("minimalReleasePluginVersion: "+minimalReleasePluginVersion)
  log.debug("actualReleasePluginVersion : "+actualReleasePluginVersion)

  if (actualReleasePluginVersion < minimalReleasePluginVersion) {
    error("org.apache.maven.plugins:maven-release-plugin version requirement not met. Expected minimal version: '${minimalReleasePluginVersion}', found: '${actualReleasePluginVersion}' ")
  }
}
