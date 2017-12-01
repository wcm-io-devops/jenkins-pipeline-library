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
package io.wcm.devops.jenkins.pipeline.utils.logging

import io.wcm.testing.jenkins.pipeline.CpsScriptTestBase
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class LoggerCpsScriptTest extends CpsScriptTestBase {

  Logger underTest = null

  @Before
  @Override
  void setUp() throws Exception {
    super.setUp()
    underTest = new Logger(this)
  }

  @Test
  void shouldRespectLogLvlNone() throws Exception {
    Logger.setLevel(LogLevel.ALL)
    Logger.setLevel(LogLevel.NONE)
    this.logAllLevels("shouldRespectLogLvlNone")
    assertLogSize(0)
  }

  @Test
  void shouldRespectLogLvlFatal() throws Exception {
    Logger.setLevel(LogLevel.FATAL)
    this.logAllLevels("shouldRespectLogLvlFatal")
    assertLogSize(1)
    assertLogContains("[FATAL]")
  }

  @Test
  void shouldRespectLogLvlError() throws Exception {
    Logger.setLevel(LogLevel.ERROR)
    this.logAllLevels("shouldRespectLogLvlError")
    assertLogSize(2)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
  }

  @Test
  void shouldRespectLogLvlWarn() throws Exception {
    Logger.setLevel(LogLevel.WARN)
    this.logAllLevels("shouldRespectLogLvlWarn")
    assertLogSize(3)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
  }

  @Test
  void shouldRespectLogLvlInfo() throws Exception {
    Logger.setLevel(LogLevel.INFO)
    LogLevel lvl = Logger.getLevel()
    this.logAllLevels("shouldRespectLogLvlInfo")

    assertLogSize(4)

    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[INFO]")
  }

  @Test
  void shouldRespectLogLvlDebug() throws Exception {
    Logger.setLevel(LogLevel.DEBUG)
    this.logAllLevels("shouldRespectLogLvlDebug")
    assertLogSize(5)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[INFO]")
    assertLogContains("[DEBUG]")
  }

  @Test
  void shouldRespectLogLvlTrace() throws Exception {
    Logger.setLevel(LogLevel.TRACE)
    this.logAllLevels("shouldRespectLogLvlTrace")
    assertLogSize(6)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[INFO]")
    assertLogContains("[DEBUG]")
    assertLogContains("[TRACE]")
  }

  @Test
  void shouldLogObject() throws Exception {
    Logger.setLevel(LogLevel.ALL)
    underTest.trace("my trace message", this)
    underTest.debug("my debug message", this)
    underTest.info("my info message", this)
    underTest.warn("my warn message", this)
    underTest.error("my error message", this)
    underTest.fatal("my fatal message", this)

    assertLogSize(6)
    assertEquals("[TRACE] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my trace message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(0))
    assertEquals("[DEBUG] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my debug message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(1))
    assertEquals("[INFO] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my info message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(2))
    assertEquals("[WARN] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my warn message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(3))
    assertEquals("[ERROR] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my error message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(4))
    assertEquals("[FATAL] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : my fatal message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest) " + this.toString(), getLogMessageAt(5))
  }

  @Test
  void shouldReturnConfiguredLevel() {
    Logger.setLevel(LogLevel.ALL)
    assertEquals(LogLevel.ALL, Logger.getLevel())
    Logger.setLevel(LogLevel.ERROR)
    assertEquals(LogLevel.ERROR, Logger.getLevel())
  }

  @Test
  void shouldUseConfiguredName() {
    Logger.setLevel(LogLevel.ALL)
    Logger testLogger = new Logger("i have a custom logger name")
    testLogger.info("i should have a custom logger name")
    assertEquals("[INFO] i have a custom logger name : i should have a custom logger name", getLogMessageAt(0))
  }

  /*@Test
  void shouldNotFailWhenNotInitialized() {
      Logger.setLevel(LogLevel.ALL)
      Logger.init(null, null)
      logAllLevels("shouldNotFailWhenNotInitialized")
  }*/

  @Test
  void shouldInstanciateWithIntegerAsLogLevel() {
    Logger.setLevel(LogLevel.ALL)
    Logger.init(this.script, 0)
    logAllLevels("shouldInstanciateWithIntegerAsLogLevel")
  }

  @Test
  void shouldInstanciateWithStringAsLogLevel() {
    Logger.setLevel(LogLevel.ALL)
    Logger.init(this.script, "INFO")
    logAllLevels("shouldInstanciateWithStringAsLogLevel")
  }

  @Test
  void shouldChangeLogLevelOnReinitialization() {
    Logger.init(this.script, "WARN")
    logAllLevels("shouldChangeLogLevelOnReinitialization - 1")
    assertEquals(LogLevel.WARN, Logger.getLevel())
    assertLogSize(3)
    Logger.setLevel(LogLevel.FATAL)
    logAllLevels("shouldChangeLogLevelOnReinitialization - 2")
    assertEquals(LogLevel.FATAL, Logger.getLevel())
    assertLogSize(4)
  }

  @Test
  void shouldLogWithColors() {
    Logger.setLevel(LogLevel.TRACE)
    this.script.setEnv("TERM", "xterm")
    logAllLevels("shouldLogWithColors")
    assertLogContains("\u001B[1;38;5;8m[TRACE]\u001B[0m")
    assertLogContains("\u001B[1;38;5;12m[DEBUG]\u001B[0m")
    assertLogContains("\u001B[1;38;5;0m[INFO]\u001B[0m")
    assertLogContains("\u001B[1;38;5;202m[WARN]\u001B[0m")
    assertLogContains("\u001B[1;38;5;5m[ERROR]\u001B[0m")
    assertLogContains("\u001B[1;38;5;9m[FATAL]\u001B[0m")
  }

  @Test
  void shouldSwitchColorOutputInstantly() {
    Logger.setLevel(LogLevel.TRACE)
    this.script.setEnv("TERM", null)
    underTest.info("without color 1")
    this.script.setEnv("TERM", "xterm")
    underTest.info("with color")
    // remove env var
    this.script.setEnv("TERM", null)
    underTest.info("without color 2")

    List<String> expectedLogMessages = [
        "[INFO] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : without color 1",
        "\u001B[1;38;5;0m[INFO]\u001B[0m io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : with color",
        "[INFO] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerCpsScriptTest : without color 2"
    ]

    assertEquals(expectedLogMessages, this.script.getDslMock().getLogMessages())
  }

  void logAllLevels(String testName) {
    underTest.trace("trace ${testName}")
    underTest.debug("debug ${testName}")
    underTest.info("info ${testName}")
    underTest.warn("warn ${testName}")
    underTest.error("error ${testName}")
    underTest.fatal("fatal ${testName}")
  }

  void assertLogSize(Integer logSize) {
    assertEquals(logSize, this.script.getLogMessages().size())
  }

  String getLogMessageAt(Integer idx) {
    return this.script.getLogMessages().get(idx)
  }

  void assertLogContains(String expected) {
    String logMessages = this.script.getLogMessages().toString()
    assertThat(logMessages, CoreMatchers.containsString(expected))
  }

}

