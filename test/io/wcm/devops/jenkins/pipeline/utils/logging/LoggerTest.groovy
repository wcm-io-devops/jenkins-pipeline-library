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

import io.wcm.testing.jenkins.pipeline.CpsScriptMock
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class LoggerTest extends DSLTestBase {
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
    this.logAllLevels()
    assertLogSize(0)
  }

  @Test
  void shouldRespectLogLvlFatal() throws Exception {
    Logger.setLevel(LogLevel.FATAL)
    this.logAllLevels()
    assertLogSize(1)
    assertLogContains("[FATAL]")
  }

  @Test
  void shouldRespectLogLvlError() throws Exception {
    Logger.setLevel(LogLevel.ERROR)
    this.logAllLevels()
    assertLogSize(2)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
  }

  @Test
  void shouldRespectLogLvlWarn() throws Exception {
    Logger.setLevel(LogLevel.WARN)
    this.logAllLevels()
    assertLogSize(3)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
  }

  @Test
  void shouldRespectLogLvlInfo() throws Exception {
    Logger.setLevel(LogLevel.INFO)
    this.logAllLevels()
    assertLogSize(5)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[DEPRECATED]")
    assertLogContains("[INFO]")
  }

  @Test
  void shouldRespectLogLvlDeprecated() throws Exception {
    Logger.setLevel(LogLevel.DEPRECATED)
    this.logAllLevels()
    assertLogSize(4)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[DEPRECATED]")
  }

  @Test
  void shouldRespectLogLvlDebug() throws Exception {
    Logger.setLevel(LogLevel.DEBUG)
    this.logAllLevels()
    assertLogSize(6)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[DEPRECATED]")
    assertLogContains("[INFO]")
    assertLogContains("[DEBUG]")
  }

  @Test
  void shouldRespectLogLvlTrace() throws Exception {
    Logger.setLevel(LogLevel.TRACE)
    this.logAllLevels()
    assertLogSize(7)
    assertLogContains("[FATAL]")
    assertLogContains("[ERROR]")
    assertLogContains("[WARN]")
    assertLogContains("[DEPRECATED]")
    assertLogContains("[INFO]")
    assertLogContains("[DEBUG]")
    assertLogContains("[TRACE]")
  }

  @Test
  void shouldLogObject() throws Exception {
    Logger.setLevel(LogLevel.ALL)
    underTest.trace("my message", this)
    underTest.debug("my message", this)
    underTest.info("my message", this)
    underTest.warn("my message", this)
    underTest.error("my message", this)
    underTest.fatal("my message", this)

    assertLogSize(6)
    assertEquals("[TRACE] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(0))
    assertEquals("[DEBUG] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(1))
    assertEquals("[INFO] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(2))
    assertEquals("[WARN] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(3))
    assertEquals("[ERROR] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(4))
    assertEquals("[FATAL] io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest : my message -> (io.wcm.devops.jenkins.pipeline.utils.logging.LoggerTest) " + this.toString(), getLogMessageAt(5))
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
      Logger.init((DSL) null, null)
      logAllLevels()
  }*/

  @Test
  void shouldInstanciateWithIntegerAsLogLevel() {
    Logger.setLevel(LogLevel.ALL)
    Logger.init(this.dslMock.getMock(), 0)
    logAllLevels()
  }

  @Test
  void shouldInstanciateWithStringAsLogLevel() {
    Logger.setLevel(LogLevel.ALL)
    Logger.init(this.dslMock.getMock(), "INFO")
    logAllLevels()
  }

  @Test
  void shouldChangeLogLevelOnReinitialization() {
    Logger.init(this.dslMock.getMock(), "WARN")
    logAllLevels()
    assertEquals(LogLevel.WARN, Logger.getLevel())
    assertLogSize(3)
    Logger.setLevel(LogLevel.FATAL)
    logAllLevels()
    assertEquals(LogLevel.FATAL, Logger.getLevel())
    assertLogSize(4)
  }

  void logAllLevels() {
    underTest.trace("trace")
    underTest.debug("debug")
    underTest.info("info")
    underTest.deprecated("deprecated")
    underTest.warn("warn")
    underTest.error("error")
    underTest.fatal("fatal")
  }

  void assertLogSize(Integer logSize) {
    logSize = logSize + 1
    assertEquals(logSize, dslMock.getLogMessages().size())
  }

  void assertLogContains(String expected) {
    String logMessages = this.dslMock.getLogMessages().toString()
    assertThat(logMessages, CoreMatchers.containsString(expected))
  }

  String getLogMessageAt(Integer idx) {
    // increase position since deprecation warning is present
    idx++
    return dslMock.getLogMessages().get(idx)
  }
}
