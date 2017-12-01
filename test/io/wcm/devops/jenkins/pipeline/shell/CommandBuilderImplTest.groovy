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

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Test

import static org.junit.Assert.assertEquals

class CommandBuilderImplTest extends DSLTestBase {

  CommandBuilderImpl underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    underTest = new CommandBuilderImpl(this.dslMock.getMock(), "underTestExec")
  }

  @Test
  void shouldBuildWithoutArguments() {
    assertEquals("underTestExec", underTest.build())
  }

  @Test
  void shouldBuildWithArguments() {
    underTest.addArgument("-B")
    underTest.addArgument("-U")
    underTest.addArgument("--global-settings", "/some/value")
    assertEquals("underTestExec -B -U --global-settings /some/value", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithCorrectPath() {
    underTest.addPathArgument('"/path variant/1"')
        .addPathArgument("'/path variant/2'")
        .addPathArgument("/path variant/3")
        .addPathArgument("--file", "'path variant/4'")
    assertEquals("underTestExec /path\\ variant/1 /path\\ variant/2 /path\\ variant/3 --file path\\ variant/4", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotAddNullPathArgument() {
    underTest.addPathArgument("nullValue", null)
    underTest.addPathArgument(null, "nullArgName")
    assertEquals("underTestExec", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotAddNullPath() {
    underTest.addPathArgument(null)
    assertEquals("underTestExec", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotAddNullArgumentAndValue() {
    underTest.addArgument("nullValue", null)
        .addArgument(null, "nullArgName")
    assertEquals("underTestExec", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotAddNullArgument() {
    underTest.addArgument("nullValue", null)
    underTest.addArgument(null, "nullArgName")
    assertEquals("underTestExec", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldNotAddEmptyArgument() {
    underTest.addArgument("")
    assertEquals("underTestExec", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithArgumentsString() {
    underTest.addArguments(null)
    underTest.addArguments("-Arg1 -Arg2")
    assertEquals("underTestExec -Arg1 -Arg2", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithArgumentsList() {
    underTest.addArguments(["-Arg1", "-Arg2", null])
    assertEquals("underTestExec -Arg1 -Arg2", underTest.build())
    assertEmptyAfterReset()
  }

  @Test
  void shouldBuildWithoutExecutable() {
    underTest = new CommandBuilderImpl(this.getDslMock().getMock())
    underTest.addPathArgument('/some/path')
    underTest.addArgument('someArg')
    assertEquals("/some/path someArg", underTest.build())
    assertEmptyAfterReset("")
  }

  @Test(expected = AbortException)
  void shouldAbortWithNullExectuable() {
    CommandBuilderImpl underTest = new CommandBuilderImpl(this.dslMock.getMock(), null)
  }

  void assertEmptyAfterReset(String expectedExecutable = "underTestExec") {
    underTest.reset()
    String resetCommandLine = underTest.build()
    assertEquals(expectedExecutable, resetCommandLine)
  }
}
