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

import io.wcm.tooling.jenkins.pipeline.credentials.Credential
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialConstants
import io.wcm.tooling.jenkins.pipeline.credentials.CredentialParser
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFile
import io.wcm.tooling.jenkins.pipeline.managedfiles.ManagedFileParser
import io.wcm.tooling.jenkins.pipeline.model.PatternMatchable
import io.wcm.tooling.jenkins.pipeline.model.Result
import io.wcm.tooling.jenkins.pipeline.model.Tool
import io.wcm.tooling.jenkins.pipeline.shell.CommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.shell.GitCommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.shell.MavenCommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.shell.ScpCommandBuilderImpl
import io.wcm.tooling.jenkins.pipeline.shell.ShellUtils
import io.wcm.tooling.jenkins.pipeline.tools.ansible.Role
import io.wcm.tooling.jenkins.pipeline.tools.ansible.RoleRequirements
import io.wcm.tooling.jenkins.pipeline.utils.IntegrationTestHelper
import io.wcm.tooling.jenkins.pipeline.utils.ListUtils
import io.wcm.tooling.jenkins.pipeline.utils.NotificationTriggerHelper
import io.wcm.tooling.jenkins.pipeline.utils.PatternMatcher
import io.wcm.tooling.jenkins.pipeline.utils.TypeUtils
import io.wcm.tooling.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.tooling.jenkins.pipeline.utils.logging.Logger
import io.wcm.tooling.jenkins.pipeline.utils.maps.MapUtils
import io.wcm.tooling.jenkins.pipeline.utils.resources.JsonLibraryResource
import io.wcm.tooling.jenkins.pipeline.utils.resources.LibraryResource
import io.wcm.tooling.jenkins.pipeline.versioning.ComparableVersion
import io.wcm.tooling.jenkins.pipeline.versioning.IntegerItem
import io.wcm.tooling.jenkins.pipeline.versioning.ListItem
import io.wcm.tooling.jenkins.pipeline.versioning.StringItem
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_ARGUMENTS
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_DESTINATION
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_EXECUTABLE
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_HOST
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_HOST_KEY_CHECK
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_PORT
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_RECURSIVE
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_SOURCE
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.SCP_USER

// job properties

properties([
    disableConcurrentBuilds(),
    pipelineTriggers([pollSCM('H * * * * ')])
])

Logger.init(this.steps, LogLevel.INFO)
Logger log = new Logger(this)

node() {

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.credentials") {
        integrationTestUtils.runTest("Credential") {
            Credential credential = new Credential("pattern","id","comment", "userName")
            integrationTestUtils.assertEquals("pattern",credential.getPattern())
        }
        integrationTestUtils.runTest("CredentialParser") {
            CredentialParser credentialParser = new CredentialParser()
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.managedfiles") {
        integrationTestUtils.runTest("ManagedFile") {
            ManagedFile managedFile = new ManagedFile("pattern","id","comment")
        }
        integrationTestUtils.runTest("ManagedFileParser") {
            ManagedFileParser managedFileParser = new ManagedFileParser()
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.model") {
        integrationTestUtils.runTest("Result") {
            Result testResult = Result.ABORTED
        }
        integrationTestUtils.runTest("Tool") {
            Tool tool = Tool.MAVEN
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.shell") {
        integrationTestUtils.runTest("CommandBuilderImpl") {
            CommandBuilderImpl commandBuilder = new CommandBuilderImpl((DSL) this.steps, "somecommand")
            commandBuilder.addArguments(["1","2"])
            commandBuilder.addPathArgument("argName", "argValue")
            commandBuilder.build()
        }
        integrationTestUtils.runTest("GitCommandBuilderImpl") {
            GitCommandBuilderImpl gitCommandBuilder = new GitCommandBuilderImpl((DSL) this.steps)
            gitCommandBuilder.addArguments(["1","2"])
            gitCommandBuilder.addPathArgument("argName", "argValue")
            gitCommandBuilder.build()
        }
        integrationTestUtils.runTest("MavenCommandBuilderImpl") {
            MavenCommandBuilderImpl mavenCommandBuilder = new MavenCommandBuilderImpl((DSL) this.steps)
            mavenCommandBuilder.addArguments(["1","2"])
            mavenCommandBuilder.addPathArgument("argName", "argValue")
            mavenCommandBuilder.build()
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
        }
        integrationTestUtils.runTest("ShellUtils") {
            String actual = ShellUtils.escapePath("folder with spaces/subfolder with spaces/filename with spaces.txt")
            integrationTestUtils.assertEquals('folder\\ with\\ spaces/subfolder\\ with\\ spaces/filename\\ with\\ spaces.txt', actual)
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.tools") {
        integrationTestUtils.runTest("ansible.Role") {
            Role role1 = new Role("src")
        }
        integrationTestUtils.runTest("ansible.RoleRequirements") {
            RoleRequirements roleRequirements = new RoleRequirements([])
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.utils.logging") {
        integrationTestUtils.runTest("Logger") {
            Logger test = new Logger(this)
            test.trace("trace")
            test.debug("debug")
            test.info("info")
            test.warn("warn")
            test.error("error")
            test.fatal("fatal")
        }
        integrationTestUtils.runTest("LogLevel") {
            LogLevel test = LogLevel.FATAL
            integrationTestUtils.assertEquals(LogLevel.FATAL, test)
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.utils.maps") {
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

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.utils.resources") {
        integrationTestUtils.runTest("JsonLibraryResource") {
            JsonLibraryResource jsonLibraryResource = new JsonLibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
            jsonLibraryResource.load()
        }
        integrationTestUtils.runTest("LibraryResource") {
            LibraryResource libraryResource = new LibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
            libraryResource.load()
        }
    }

    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.utils") {
        integrationTestUtils.runTest("ListUtils") {
            List test = [1,2,3]
            test = ListUtils.removeAt(test, 1)
            integrationTestUtils.assertEquals([1,3],test)
        }
        integrationTestUtils.runTest("NotificationTriggerHelper") {
            NotificationTriggerHelper notificationTriggerHelper = new NotificationTriggerHelper("SUCCESS","FAILURE")
            Result result = notificationTriggerHelper.getTrigger()
            integrationTestUtils.assertEquals(Result.FIXED, result)
        }
        integrationTestUtils.runTest("PatternMatcher") {
            List<PatternMatchable> testList = []
            ManagedFile file1 = new ManagedFile("file1","id-file1","name-file1","comment-file1")
            ManagedFile file2 = new ManagedFile("file2","id-file2","name-file2","comment-file2")
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
    integrationTestUtils.integrationTestUtils.runTestsOnPackage("io.wcm.tooling.jenkins.pipeline.utils.versioning") {
        List<String> versionQualifier =
            ["1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2",
             "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot",
             "1-1", "1-2", "1-123"]

        List<String> versionNumber =
            ["2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1",
             "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m"]
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

        integrationTestUtils.runTest("IntegerItem") {
            IntegerItem integerItem = new IntegerItem("1")
        }
        integrationTestUtils.runTest("ListItem") {
            ListItem listItem = new ListItem()
        }
        integrationTestUtils.runTest("StringItem") {
            StringItem stringItem = new StringItem("2",false)
        }
    }

    stage("Result overview") {
        integrationTestUtils.logTestResults(IntegrationTestHelper.getResults())
    }
    stage("Check") {
        integrationTestUtils.processFailedTests(IntegrationTestHelper.getResults())
    }
}
