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
package vars.wrap

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.testing.jenkins.pipeline.StepConstants
import io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import org.junit.Assert
import org.junit.Test

class WrapColorIT extends LibraryIntegrationTestBase {

  @Test
  void shouldWrapColorMultiWithConfig() {
    Logger.initialized = false
    loadAndExecuteScript("vars/wrap/jobs/shouldWrapColorMultiWithConfigTestJob.groovy")

    List<String> expectedLogOutputs = [
        "[INFO] vars.wrap.jobs.shouldWrapColorMultiWithConfigTestJob : non colorized output - 1",
        "[DEBUG] wrap : Wrapping build with color scheme: 'gnome-terminal'",
        "\u001B[1;38;5;0m[INFO]\u001B[0m vars.wrap.jobs.shouldWrapColorMultiWithConfigTestJob : first wrap env.TERM: gnome-terminal",
        "\u001B[1;38;5;12m[DEBUG]\u001B[0m wrap : Wrapping build with color scheme: 'vga'",
        "\u001B[1;38;5;0m[INFO]\u001B[0m vars.wrap.jobs.shouldWrapColorMultiWithConfigTestJob : second wrap env.TERM: vga",
        "[INFO] vars.wrap.jobs.shouldWrapColorMultiWithConfigTestJob : non colorized output - 2"
    ]

    StepRecorderAssert.assertTwice(StepConstants.ANSI_COLOR)

    List<String> actualLogOutputs = this.context.getDslMock().getLogMessages()
    Assert.assertEquals(expectedLogOutputs, actualLogOutputs)
    Assert.assertNull(null, this.getEnv('TERM'))
  }

  @Test
  void shouldWrapColor() {
    Logger.initialized = false
    loadAndExecuteScript("vars/wrap/jobs/shouldWrapColorTestJob.groovy")
    List<String> expectedLogOutputs = [
        "[INFO] vars.wrap.jobs.shouldWrapColorTestJob : non colorized output - 1",
        "[DEBUG] wrap : Wrapping build with color scheme: 'xterm'",
        "\u001B[1;38;5;0m[INFO]\u001B[0m vars.wrap.jobs.shouldWrapColorTestJob : colorized output",
        "[INFO] vars.wrap.jobs.shouldWrapColorTestJob : non colorized output - 2",
    ]

    StepRecorderAssert.assertOnce(StepConstants.ANSI_COLOR)

    List<String> actualLogOutputs = this.context.getDslMock().getLogMessages()
    Assert.assertEquals(expectedLogOutputs, actualLogOutputs)
    Assert.assertNull(this.getEnv('TERM'))
  }

  @Test
  void shouldWrapColorOnlyOnceWithSameColorMode() {
    Logger.initialized = false
    loadAndExecuteScript("vars/wrap/jobs/shouldWrapColorOnlyOnceWithSameColorModeTestJob.groovy")

    List<String> expectedLogOutputs = [
        "[INFO] vars.wrap.jobs.shouldWrapColorOnlyOnceWithSameColorModeTestJob : non colorized output - 1",
        "[DEBUG] wrap : Wrapping build with color scheme: 'vga'",
        "\u001B[1;38;5;0m[INFO]\u001B[0m vars.wrap.jobs.shouldWrapColorOnlyOnceWithSameColorModeTestJob : first wrap env.TERM: vga",
        "\u001B[1;38;5;12m[DEBUG]\u001B[0m wrap : Do not wrap with color scheme: 'vga' because wrapper with same color map is already active",
        "\u001B[1;38;5;0m[INFO]\u001B[0m vars.wrap.jobs.shouldWrapColorOnlyOnceWithSameColorModeTestJob : second wrap env.TERM: vga",
        "[INFO] vars.wrap.jobs.shouldWrapColorOnlyOnceWithSameColorModeTestJob : non colorized output - 2"
    ]

    StepRecorderAssert.assertOnce(StepConstants.ANSI_COLOR)

    List<String> actualLogOutputs = this.context.getDslMock().getLogMessages()
    Assert.assertEquals(expectedLogOutputs, actualLogOutputs)
    Assert.assertNull(null, this.getEnv('TERM'))
  }

}
