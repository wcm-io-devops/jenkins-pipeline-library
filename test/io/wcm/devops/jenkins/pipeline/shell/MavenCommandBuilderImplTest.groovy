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
package io.wcm.devops.jenkins.pipeline.shell

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Test

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class MavenCommandBuilderImplTest extends DSLTestBase {

  MavenCommandBuilderImpl underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    underTest = new MavenCommandBuilderImpl(this.dslMock.getMock())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildDefaultCommand() {
    assertEquals(MavenCommandBuilderImpl.EXECUTABLE, underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithCustomExecutable() {
    underTest = new MavenCommandBuilderImpl(this.dslMock.getMock(),[:], "time mvn")
    underTest.setPom("customPom1.xml")
    underTest.setGoals("clean package")
    assertEquals("time mvn -f customPom1.xml clean package", underTest.build())
    assertEmptyAfterReset("time mvn")
  }

  @Test
  void shouldBuildWithFile() {
    underTest.setPom("customPom1.xml")
    underTest.setGoals("clean package")
    assertEquals("mvn -f customPom1.xml clean package", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithCustomSettings() {
    underTest.setPom("customPom2.xml")
    underTest.setGoals("clean package")
    underTest.setGlobalSettings("/path/to/global settings.xml")
    underTest.setSettings("/path/to/settings.xml")
    assertEquals("mvn -f customPom2.xml clean package --global-settings /path/to/global\\ settings.xml --settings /path/to/settings.xml", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithManualDefines() {
    underTest.setGoals("clean package")
    underTest.addDefine(null)
    underTest.addDefine(null, null)
    underTest.addDefine(null, "value")
    underTest.addDefine("defineFlag")
    underTest.addDefine("booleanDefine", true)
    underTest.addDefine("stringDefine", "stringValue")
    underTest.addDefine("defineFlagWithNull", null)
    assertEquals("mvn clean package -DdefineFlag -DbooleanDefine=true -DstringDefine=stringValue -DdefineFlagWithNull", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithStringDefines() {
    underTest.setGoals("clean package")
    underTest.addDefines("-Dflag -Dname=value")
    assertEquals("mvn clean package -Dflag -Dname=value", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithMapDefines() {
    underTest.setGoals("clean package")

    Map map = [:]
    map.put("flag", null)
    map.put("string", "value")
    map.put("booleanTrue", true)
    map.put("booleanFalse", false)

    underTest.addDefines(map)
    assertEquals("mvn clean package -Dflag -Dstring=value -DbooleanTrue=true -DbooleanFalse=false", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotFailOnEmptyConfig() {
    underTest.applyConfig([:])
    assertEquals("mvn", underTest.build())
  }

  @Test
  void shouldApplyConfigVariant1() {
    Map config = [
        (MAVEN): [
            (MAVEN_DEFINES)        : "-DvalueDefine=value -DflagDefine",
            (MAVEN_ARGUMENTS)      : "-B -U",
            (MAVEN_GOALS)          : "clean install",
            (MAVEN_EXECUTABLE)     : "path/to/custom/maven/bin/mvn",
            (MAVEN_GLOBAL_SETTINGS): "global-settings-id",
            (MAVEN_POM)            : "path with spaces/to/custom/pom.xml",
            (MAVEN_SETTINGS)       : "settings-id",
            (MAVEN_PROFILES)       : ["profile1", "profile2"],
            (MAVEN_RETURN_STATUS)  : true
        ]
    ]
    underTest.applyConfig(config)
    assertEquals("path/to/custom/maven/bin/mvn -f path\\ with\\ spaces/to/custom/pom.xml clean install -B -U -DvalueDefine=value -DflagDefine -Pprofile1,profile2", underTest.build())
    assertEquals("settings-id", underTest.getSettingsId())
    assertEquals("global-settings-id", underTest.getGlobalSettingsId())
    assertFalse(underTest.getReturnStdout())
    assertTrue(underTest.getReturnStatus())
    assertEmptyAfterReset("path/to/custom/maven/bin/mvn")
  }

  @Test
  void shouldApplyConfigVariant2() {
    Map config = [
        (MAVEN): [
            (MAVEN_DEFINES)        : [valueDefine: "value", "flagDefine": null],
            (MAVEN_ARGUMENTS)      : ["-B", "-U"],
            (MAVEN_GOALS)          : ["clean", "install"],
            (MAVEN_EXECUTABLE)     : "path/to/custom/maven/bin/mvn",
            (MAVEN_GLOBAL_SETTINGS): "global-settings-id",
            (MAVEN_POM)            : "path with spaces/to/custom/pom.xml",
            (MAVEN_SETTINGS)       : "settings-id",
            (MAVEN_PROFILES)       : "profile3,profile4",
            (MAVEN_RETURN_STDOUT)  : true
        ]
    ]
    underTest.applyConfig(config)
    assertEquals("path/to/custom/maven/bin/mvn -f path\\ with\\ spaces/to/custom/pom.xml clean install -B -U -DvalueDefine=value -DflagDefine -Pprofile3,profile4", underTest.build())
    assertEquals("settings-id", underTest.getSettingsId())
    assertEquals("global-settings-id", underTest.getGlobalSettingsId())
    assertTrue(underTest.getReturnStdout())
    assertFalse(underTest.getReturnStatus())
    assertEmptyAfterReset("path/to/custom/maven/bin/mvn")
  }

  @Test
  void shouldBuildWithParams() {
    Map params = ([choiceParam: "choice1", boolParam: true, stringParam: "text"])
    underTest = new MavenCommandBuilderImpl(this.dslMock.getMock(), params)
    Map config = [
        (MAVEN): [
            (MAVEN_INJECT_PARAMS): true,
            (MAVEN_GOALS)        : ["clean", "install"],
        ]
    ]
    underTest.applyConfig(config)
    assertEquals("mvn clean install -DchoiceParam=choice1 -DboolParam=true -DstringParam=text", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithoutParams() {
    Map params = ([choiceParam: "choice1", boolParam: true, stringParam: "text"])
    underTest = new MavenCommandBuilderImpl(this.dslMock.getMock(), params)
    Map config = [
        (MAVEN): [
            (MAVEN_INJECT_PARAMS): false,
            (MAVEN_GOALS)        : ["clean", "install"],
        ]
    ]
    underTest.applyConfig(config)

    assertEquals("mvn clean install", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithGoalList() {
    underTest.setGoals(["clean", "package"])
    assertEquals("mvn clean package", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithEmptyGoalList() {
    underTest.setGoals([])
    assertEquals("mvn", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldAddProfilesFromString() {
    underTest.addProfiles("profile1,profile2")
    assertEquals("mvn -Pprofile1,profile2", underTest.build())
    underTest.reset()
    underTest.applyConfig(
        (MAVEN): [
            (MAVEN_PROFILES): "profile3,profile4"
        ]
    )
    assertEquals("mvn -Pprofile3,profile4", underTest.build())
    underTest.reset()
    underTest.applyConfig(
        (MAVEN): [
            (MAVEN_PROFILES): ""
        ]
    )
    assertEquals("mvn", underTest.build())
  }

  @Test
  void shouldAddProfilesFromList() {
    underTest.addProfiles(["profile5", "profile6"])
    assertEquals("mvn -Pprofile5,profile6", underTest.build())
    underTest.reset()
    underTest.applyConfig(
        (MAVEN): [
            (MAVEN_PROFILES): ["profile7", "profile8"]
        ]
    )
    assertEquals("mvn -Pprofile7,profile8", underTest.build())
    underTest.reset()
    underTest.applyConfig(
        (MAVEN): [
            (MAVEN_PROFILES): []
        ]
    )
    assertEquals("mvn", underTest.build())
  }

  void assertEmptyAfterReset(String expectedExecutable = "mvn") {
    underTest.reset()
    String resetCommandLine = underTest.build()
    assertEquals(expectedExecutable, resetCommandLine)
    assertEquals(null, underTest.getSettingsId())
    assertEquals(null, underTest.getGlobalSettingsId())
  }
}
