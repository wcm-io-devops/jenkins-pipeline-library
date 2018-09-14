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
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

// job properties

properties([
  disableConcurrentBuilds(),
  pipelineTriggers([pollSCM('H * * * * ')])
])

Logger.init(this, LogLevel.INFO)
Logger log = new Logger(this)

node() {

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.credentials") {
    integrationTestUtils.runTest("Credential") {
      Credential credential

      credential = new Credential("pattern", "id", "comment", "userName")
      integrationTestUtils.assertEquals("pattern", credential.getPattern())
      integrationTestUtils.assertEquals("id", credential.getId())
      integrationTestUtils.assertEquals("comment", credential.getComment())
      integrationTestUtils.assertEquals("userName", credential.getUserName())

      credential = new Credential("pattern", "id", "comment")
      integrationTestUtils.assertEquals("pattern", credential.getPattern())
      integrationTestUtils.assertEquals("id", credential.getId())
      integrationTestUtils.assertEquals("comment", credential.getComment())
      integrationTestUtils.assertNull(credential.getUserName())

      credential = new Credential("pattern", "id")
      integrationTestUtils.assertEquals("pattern", credential.getPattern())
      integrationTestUtils.assertEquals("id", credential.getId())
      integrationTestUtils.assertNull(credential.getComment())
      integrationTestUtils.assertNull(credential.getUserName())
    }
    integrationTestUtils.runTest("CredentialConstants") {
      log.info(CredentialConstants.SCM_CREDENTIALS_PATH, CredentialConstants.SCM_CREDENTIALS_PATH)
      log.info(CredentialConstants.HTTP_CREDENTIALS_PATH, CredentialConstants.HTTP_CREDENTIALS_PATH)
      log.info(CredentialConstants.SCM_CREDENTIALS_PATH, CredentialConstants.SSH_CREDENTIALS_PATH)
    }
    integrationTestUtils.runTest("CredentialParser") {
      CredentialParser credentialParser = new CredentialParser()
      String jsonString = '''
        [
          {
            "pattern": "jsonPattern",
            "id": "jsonId",
            "comment": "jsonComment",
            "username": "jsonUsername"
          }
        ]
      '''
      JSON credentialJson = readJSON(text: jsonString)
      credentialParser.parse(credentialJson)
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.environment") {
    integrationTestUtils.runTest("EnvironmentConstants") {
      log.info(EnvironmentConstants.BRANCH_NAME,  EnvironmentConstants.BRANCH_NAME)
      log.info(EnvironmentConstants.GIT_BRANCH,  EnvironmentConstants.GIT_BRANCH)
      log.info(EnvironmentConstants.SCM_URL,  EnvironmentConstants.SCM_URL)
      log.info(EnvironmentConstants.TERM,  EnvironmentConstants.TERM)
      log.info(EnvironmentConstants.WORKSPACE,  EnvironmentConstants.WORKSPACE)
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.managedfiles") {
    integrationTestUtils.runTest("ManagedFile") {
      ManagedFile managedFile

      managedFile = new ManagedFile("pattern", "id", "name", "comment")
      integrationTestUtils.assertEquals("pattern", managedFile.getPattern())
      integrationTestUtils.assertEquals("id", managedFile.getId())
      integrationTestUtils.assertEquals("name", managedFile.getName())
      integrationTestUtils.assertEquals("comment", managedFile.getComment())

      managedFile = new ManagedFile("pattern", "id", "name")
      integrationTestUtils.assertEquals("pattern", managedFile.getPattern())
      integrationTestUtils.assertEquals("id", managedFile.getId())
      integrationTestUtils.assertEquals("name", managedFile.getName())
      integrationTestUtils.assertNull(managedFile.getComment())

      managedFile = new ManagedFile("pattern", "id")
      integrationTestUtils.assertEquals("pattern", managedFile.getPattern())
      integrationTestUtils.assertEquals("id", managedFile.getId())
      integrationTestUtils.assertNull(managedFile.getName())
      integrationTestUtils.assertNull(managedFile.getComment())
    }
    integrationTestUtils.runTest("ManagedFileConstants") {
      log.info(ManagedFileConstants.GLOBAL_MAVEN_SETTINGS_PATH,  ManagedFileConstants.GLOBAL_MAVEN_SETTINGS_PATH)
      log.info(ManagedFileConstants.GLOBAL_MAVEN__SETTINGS_ENV,  ManagedFileConstants.GLOBAL_MAVEN__SETTINGS_ENV)
      log.info(ManagedFileConstants.MAVEN_SETTINS_PATH,  ManagedFileConstants.MAVEN_SETTINS_PATH)
      log.info(ManagedFileConstants.MAVEN_SETTING_ENV,  ManagedFileConstants.MAVEN_SETTING_ENV)
      log.info(ManagedFileConstants.NPM_CONFIG_USERCONFIG_PATH,  ManagedFileConstants.NPM_CONFIG_USERCONFIG_PATH)
      log.info(ManagedFileConstants.NPM_CONFIG_USERCONFIG_ENV,  ManagedFileConstants.NPM_CONFIG_USERCONFIG_ENV)
      log.info(ManagedFileConstants.NPM_CONF_USERCONFIG_ENV,  ManagedFileConstants.NPM_CONF_USERCONFIG_ENV)
      log.info(ManagedFileConstants.NPMRC_PATH,  ManagedFileConstants.NPMRC_PATH)
      log.info(ManagedFileConstants.NPMRC_ENV,  ManagedFileConstants.NPMRC_ENV)
      log.info(ManagedFileConstants.NPM_CONF_GLOBALCONFIG_ENV,  ManagedFileConstants.NPM_CONF_GLOBALCONFIG_ENV)
      log.info(ManagedFileConstants.BUNDLE_CONFIG_ENV,  ManagedFileConstants.BUNDLE_CONFIG_ENV)
      log.info(ManagedFileConstants.BUNDLE_CONFIG_PATH,  ManagedFileConstants.BUNDLE_CONFIG_PATH)

    }
    integrationTestUtils.runTest("ManagedFileParser") {
      ManagedFileParser managedFileParser = new ManagedFileParser()
      String managedFileJsonString = '''
        [
          {
            "pattern": "jsonPattern",
            "id": "jsonId",
            "comment": "jsonComment",
            "name": "jsonName"
          }
        ]
      '''
      JSON managedFileJson = readJSON(text: managedFileJsonString)
      managedFileParser.parse(managedFileJson)
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.model") {
    integrationTestUtils.runTest("Result") {
      Result testResult
      testResult = Result.NOT_BUILD
      testResult = Result.ABORTED
      testResult = Result.FAILURE
      testResult = Result.UNSTABLE
      testResult = Result.SUCCESS
      testResult = Result.STILL_FAILING
      testResult = Result.STILL_UNSTABLE
      testResult = Result.FIXED
      testResult = Result.fromString("STILL FAILING")
      log.info(Result.FIXED.toString(), Result.FIXED)

      integrationTestUtils.assertTrue(Result.NOT_BUILD.isWorseOrEqualTo(Result.NOT_BUILD), "result assertion 1")
      integrationTestUtils.assertTrue(Result.ABORTED.isWorseThan(Result.NOT_BUILD), "result assertion 2")
      integrationTestUtils.assertTrue(Result.SUCCESS.isBetterThan(Result.ABORTED), "result assertion 3")
      integrationTestUtils.assertTrue(Result.SUCCESS.isBetterOrEqualTo(Result.SUCCESS), "result assertion 4")
    }
    integrationTestUtils.runTest("Tool") {
      Tool tool
      tool = Tool.MAVEN
      tool = Tool.JDK
      tool = Tool.ANSIBLE
      tool = Tool.GIT
      tool = Tool.GROOVY
      tool = Tool.MSBUILD
      tool = Tool.ANT
      tool = Tool.PYTHON
      tool = Tool.DOCKER
      tool = Tool.NODEJS
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.scm") {
    integrationTestUtils.runTest("GitRepository") {
      GitRepository gitRepo = new GitRepository(this, "https://github.com/wcm-io-devops/jenkins-pipeline-library.git")
      integrationTestUtils.assertEquals(GitRepository.PROTOCOL_HTTPS, gitRepo.getProtocol())
      integrationTestUtils.assertNotEquals(GitRepository.PROTOCOL_HTTP, gitRepo.getProtocol())
      integrationTestUtils.assertNotEquals(GitRepository.PROTOCOL_SSH, gitRepo.getProtocol())

      gitRepo.getGroup()
      gitRepo.getProject()
      gitRepo.getProjectName()
      gitRepo.getProtocolPrefix()
      gitRepo.getServer()
      gitRepo.getUsername()
      gitRepo.isSsh()
      gitRepo.isHttp()
      gitRepo.isHttps()
      gitRepo.getUrl()
      gitRepo.isValid()
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.shell") {
    integrationTestUtils.runTest("CommandBuilderImpl") {
      CommandBuilderImpl commandBuilder
      commandBuilder = new CommandBuilderImpl((DSL) this.steps, "somecommand")
      commandBuilder. setExecutable("someexecutable")
      commandBuilder.addArgument("arg0")
      commandBuilder.addArgument("arg1","arg1Value")
      commandBuilder.addArguments(["arg2", "arg3"])
      commandBuilder.addArguments("arg4 arg5")
      commandBuilder.addPathArgument("pathArg0")
      commandBuilder.addPathArgument("pathArg1", "pathArg1Value")
      commandBuilder.build()
      commandBuilder.reset()

      commandBuilder = new CommandBuilderImpl((DSL) this.steps)
    }
    integrationTestUtils.runTest("GitCommandBuilderImpl") {
      GitCommandBuilderImpl gitCommandBuilder = new GitCommandBuilderImpl((DSL) this.steps)
      gitCommandBuilder.setExecutable("git")
      gitCommandBuilder.addArgument("arg0")
      gitCommandBuilder.addArgument("arg1","arg1Value")
      gitCommandBuilder.addArguments(["arg2", "arg3"])
      gitCommandBuilder.addArguments("arg4 arg5")
      gitCommandBuilder.addPathArgument("pathArg0")
      gitCommandBuilder.addPathArgument("pathArg1", "pathArg1Value")
      gitCommandBuilder.build()
      gitCommandBuilder.reset()
    }
    integrationTestUtils.runTest("MavenCommandBuilderImpl") {
      MavenCommandBuilderImpl mavenCommandBuilder
      mavenCommandBuilder = new MavenCommandBuilderImpl((DSL) this.steps)
      mavenCommandBuilder.setExecutable("mvn")
      mavenCommandBuilder.addArgument("arg0")
      mavenCommandBuilder.addArgument("arg1","arg1Value")
      mavenCommandBuilder.addArguments(["arg2", "arg3"])
      mavenCommandBuilder.addArguments("arg4 arg5")
      mavenCommandBuilder.addPathArgument("pathArg0")
      mavenCommandBuilder.addPathArgument("pathArg1", "pathArg1Value")
      mavenCommandBuilder.setGlobalSettings("globalSettingsPath")
      mavenCommandBuilder.setGlobalSettingsId("globalSettingsId")
      mavenCommandBuilder.setSettings("settingsPath")
      mavenCommandBuilder.setSettingsId("settingsId")
      mavenCommandBuilder.addProfiles("profile1,profile2")
      mavenCommandBuilder.addProfiles(["profile3","profile4"])
      mavenCommandBuilder.setGoals("goal1 goal2")
      mavenCommandBuilder.setGoals(["goal3","goal4"])
      mavenCommandBuilder.setPom("pompath/pom.xml")
      mavenCommandBuilder.addDefine("define1")
      mavenCommandBuilder.addDefine("define2","define2Value")
      mavenCommandBuilder.addDefines("-Ddefine3 -Ddefine4=define4Value")
      mavenCommandBuilder.addDefines([ define5: "defineValue5", define6: null ])
      Map mvnConfig = [
        (MAVEN) : [
          (MAVEN_ARGUMENTS): [ "-B", "-U" ],
          (MAVEN_DEFINES): ["name": "value", "flag": null],
          (MAVEN_EXECUTABLE): "/path/to/maven/bin",
          (MAVEN_GLOBAL_SETTINGS): "managed-file-id",
          (MAVEN_GOALS): ["goal1", "goal2"],
          (MAVEN_INJECT_PARAMS): false,
          (MAVEN_POM): "/path/to/pom.xml",
          (MAVEN_PROFILES): ["profile1", "profile2"],
          (MAVEN_SETTINGS): "managed-file-id",
        ]
      ]
      mavenCommandBuilder.applyConfig(mvnConfig)
      mavenCommandBuilder.build()
      mavenCommandBuilder.reset()

      mavenCommandBuilder = new MavenCommandBuilderImpl((DSL) this.steps, "mvn")

      mavenCommandBuilder = new MavenCommandBuilderImpl((DSL) this.steps, [:])
      mavenCommandBuilder = new MavenCommandBuilderImpl((DSL) this.steps, [:], "mvn")
    }
    integrationTestUtils.runTest("ScpCommandBuilderImpl") {
      ScpCommandBuilderImpl scpCommandBuilder = new ScpCommandBuilderImpl((DSL) this.steps)
      Map configTemplate = [
        (SCP_HOST)          : "testhost",
        (SCP_PORT)          : null,
        (SCP_USER)          : null,
        (SCP_ARGUMENTS)     : [],
        (SCP_RECURSIVE)     : false,
        (SCP_SOURCE)        : "/path/to/source/*",
        (SCP_DESTINATION)   : "/path/to/destination",
        (SCP_EXECUTABLE)    : null,
        (SCP_HOST_KEY_CHECK): false
      ]

      scpCommandBuilder.applyConfig(configTemplate)

      integrationTestUtils.assertEquals('scp -P 22 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null /path/to/source/* testhost:"/path/to/destination"', scpCommandBuilder.build())

      scpCommandBuilder = new ScpCommandBuilderImpl((DSL) this.steps, "scp")
      scpCommandBuilder.setExecutable("scp")
      scpCommandBuilder.setHost("host")
      scpCommandBuilder.setDestinationPath("destinationPath")
      scpCommandBuilder.setSourcePath("sourcePath")
      scpCommandBuilder.addArgument("arg0")
      scpCommandBuilder.addArgument("arg1","arg1Value")
      scpCommandBuilder.addArguments(["arg2", "arg3"])
      scpCommandBuilder.addArguments("arg4 arg5")
      scpCommandBuilder.addPathArgument("pathArg0")
      scpCommandBuilder.addPathArgument("pathArg1", "pathArg1Value")
      scpCommandBuilder.build()
      scpCommandBuilder.reset()
      Credential scpCredential1 = new Credential("pattern","id","comment", "username")
      scpCommandBuilder.setCredential(scpCredential1)
    }
    integrationTestUtils.runTest("ShellUtils") {
      ShellUtils.trimDoubleQuote('""val"ue""')
      ShellUtils.trimSingleQuote("''val'ue''")
      ShellUtils.escapeShellCharacters("\\ '\"!#\$&(),;<>?[]^`{|}")
      String actual = ShellUtils.escapePath("folder with spaces/subfolder with spaces/filename with spaces.txt")
      integrationTestUtils.assertEquals('folder\\ with\\ spaces/subfolder\\ with\\ spaces/filename\\ with\\ spaces.txt', actual)
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.tools") {
    integrationTestUtils.runTest("ansible.Role") {
      Role role1 = new Role("src")
      role1.isValid()
      role1.isGalaxyRole()
      role1.isScmRole()
      log.info(Role.SCM_GIT,Role.SCM_GIT)
    }
    integrationTestUtils.runTest("ansible.RoleRequirements") {
      List ymlContent = [
        [
          src: "https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library.git",
          scm: "git",
          name: "wcm_io_devops.jenkins_pipeline_library",
          version: "1.0.0",
        ]
      ]
      RoleRequirements roleRequirements = new RoleRequirements(ymlContent)
      roleRequirements.parse()
      roleRequirements.getCheckoutConfigs()
      roleRequirements.getRoles()
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.utils.logging") {
    integrationTestUtils.runTest("Logger") {
      Logger test = new Logger(this)
      // call all logger init functions
      Logger.init((DSL) this.steps, [:])
      Logger.init((DSL) this.steps, "info")
      Logger.init((DSL) this.steps, 0)
      Logger.init(this, [:])
      Logger.init(this, "info")
      Logger.init(this, 0)
      Logger.setLevel(LogLevel.ALL)
      Logger.setLevel(LogLevel.DEBUG)
      Logger.setLevel(LogLevel.WARN)
      Logger.setLevel(LogLevel.ERROR)
      Logger.setLevel(LogLevel.DEPRECATED)
      Logger.setLevel(LogLevel.FATAL)
      Logger.setLevel(LogLevel.INFO)
      Logger.setLevel(LogLevel.NONE)
      Logger.setLevel(LogLevel.TRACE)
      // set loglevel to all
      Logger.setLevel(LogLevel.ALL)

      test.trace("trace")
      test.debug("debug")
      test.info("info")
      test.warn("warn")
      test.error("error")
      test.fatal("fatal")

      test.trace("trace", this)
      test.debug("debug", this)
      test.deprecated("deprecatedItem", "deprecatedMessage")
      test.info("info", this)
      test.warn("warn", this)
      test.error("error", this)
      test.fatal("fatal", this)
    }
    integrationTestUtils.runTest("LogLevel") {
      LogLevel test = LogLevel.FATAL
      integrationTestUtils.assertEquals(LogLevel.FATAL, test)

      LogLevel testLogLevel = LogLevel.ALL
      testLogLevel = LogLevel.DEBUG
      testLogLevel = LogLevel.DEPRECATED
      testLogLevel = LogLevel.ERROR
      testLogLevel = LogLevel.WARN
      testLogLevel = LogLevel.DEBUG
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.utils.maps") {
    integrationTestUtils.runTest("MapUtils") {
      Map map1 = [
        node1: [
          subnode11: [
            prop111: "value111",
            prop112: "value112",
          ],
          prop1    : 1
        ],
        node2: [
          prop1    : 21,
          subnode21: [
            prop21: "value21"
          ]
        ]
      ]
      Map map2 = [
        node1: [
          subnode11: [
            prop111: "value111NEW",
            prop113: "value113"
          ],
          prop2    : 12
        ],
        node2: [
          prop1: "21NEW",
        ]
      ]

      Map expected = [
        node1: [
          subnode11: [
            prop111: "value111NEW",
            prop112: "value112",
            prop113: "value113"
          ],
          prop1    : 1,
          prop2    : 12
        ],
        node2: [
          prop1    : "21NEW",
          subnode21: [
            prop21: "value21"
          ]
        ]
      ]

      Map actual = MapUtils.merge(map1, map2)
      integrationTestUtils.assertEquals(expected, actual)
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.utils.resources") {
    integrationTestUtils.runTest("JsonLibraryResource") {
      JsonLibraryResource jsonLibraryResource = new JsonLibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
      jsonLibraryResource.load()
    }
    integrationTestUtils.runTest("LibraryResource") {
      LibraryResource libraryResource = new LibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
      libraryResource.load()
    }
  }

  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.utils") {
    integrationTestUtils.runTest("ListUtils") {
      List test = [1, 2, 3]
      test = ListUtils.removeAt(test, 1)
      integrationTestUtils.assertEquals([1, 3], test)
    }
    integrationTestUtils.runTest("NotificationTriggerHelper") {
      NotificationTriggerHelper notificationTriggerHelper = new NotificationTriggerHelper("SUCCESS", "FAILURE")
      Result result = notificationTriggerHelper.getTrigger()
      integrationTestUtils.assertEquals(Result.FIXED, result)
    }
    integrationTestUtils.runTest("PatternMatcher") {
      List<PatternMatchable> testList = []
      ManagedFile file1 = new ManagedFile("file1", "id-file1", "name-file1", "comment-file1")
      ManagedFile file2 = new ManagedFile("file2", "id-file2", "name-file2", "comment-file2")
      testList.push(file1)
      testList.push(file2)
      PatternMatcher patternMatcher = new PatternMatcher()
      PatternMatchable foundFile = patternMatcher.getBestMatch("file1", testList)
      integrationTestUtils.assertEquals(file1, foundFile)
    }
    integrationTestUtils.runTest("TypeUtils") {
      TypeUtils typeUtils = new TypeUtils()
      integrationTestUtils.assertEquals(true, typeUtils.isMap([:]))
      integrationTestUtils.assertEquals(false, typeUtils.isMap("noMap"))
      integrationTestUtils.assertEquals(true, typeUtils.isList([]))
      integrationTestUtils.assertEquals(false, typeUtils.isList("noList"))
    }
  }
  integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.devops.jenkins.pipeline.utils.versioning") {
    List<String> versionQualifier =
      ["1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2",
       "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot",
       "1-1", "1-2", "1-123"]

    List<String> versionNumber =
      ["2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1",
       "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m"]


    integrationTestUtils.runTest("IntegerItem") {
      IntegerItem integerItem = new IntegerItem("1")
    }
    integrationTestUtils.runTest("ListItem") {
      ListItem listItem = new ListItem()
    }
    integrationTestUtils.runTest("StringItem") {
      StringItem stringItem = new StringItem("2", false)
    }

    versionQualifier.each {
      String value ->
        integrationTestUtils.runTest("ComparableVersion '$value' (qualifier)") {
          ComparableVersion comparableVersion = new ComparableVersion(value)
        }
    }
    versionNumber.each {
      String value ->
        integrationTestUtils.runTest("ComparableVersion '$value' (version)") {
          ComparableVersion comparableVersion = new ComparableVersion(value)
        }
    }


  }

  stage("Result overview") {
    integrationTestUtils.logTestResults(IntegrationTestHelper.getResults())
  }
  stage("Check") {
    integrationTestUtils.processFailedTests(IntegrationTestHelper.getResults())
  }
}
