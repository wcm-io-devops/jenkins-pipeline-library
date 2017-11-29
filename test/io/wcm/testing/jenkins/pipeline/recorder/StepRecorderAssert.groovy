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
package io.wcm.testing.jenkins.pipeline.recorder

import io.wcm.testing.jenkins.pipeline.StepConstants

import static org.junit.Assert.assertEquals

/**
 * Assert functions for the StepRecorder to make the evaluation of executed steps easier
 */
class StepRecorderAssert {

  private static StepRecorder rec

  /**
   * Has to be called once before test execution to provide the reference to the StepRecorder
   * @param rec
   * @return
   */
  static init(StepRecorder rec) {
    this.rec = rec
  }

  /**
   * Asserts that a step with the given stepName was called the given amount of times
   * When the assertion is correct the list of detected steps is returned for further processing in the test
   *
   * @param stepName The name of the step that the assertion should look for
   * @param stepCount The amount of times the step is allowed to be recorded
   * @return List of recorded step calls
   */
  static List assertStepCalls(String stepName, Integer stepCount) {
    List recordedSteps = rec.getRecordedSteps(stepName)
    assertEquals(String.format("step '%s' expected %s time(s), actualSteps '%s'", stepName, stepCount, recordedSteps.toString()), stepCount, recordedSteps.size())
    return recordedSteps
  }

  /**
   * Asserts that the step identified by the stepName was not recorded by the StepRecorder
   *
   * @param stepName The name of the step that the assertion should look for
   */
  static void assertNone(String stepName) {
    assertStepCalls(stepName, 0)
  }

  /**
   * Asserts that the step identified by the stepName was recorded once by the StepRecorder
   *
   * @param stepName The name of the step that the assertion should look for
   * @return The recorded step
   */
  static Object assertOnce(String stepName) {
    return assertStepCalls(stepName, 1).get(0)
  }

  /**
   * Asserts that the step identified by the stepName was recorded twice by the StepRecorder
   *
   * @param stepName The name of the step that the assertion should look for
   * @return The list of the recorded steps
   */
  static List assertTwice(String stepName) {
    return assertStepCalls(stepName, 2)
  }

  /**
   * Asserts that the shell step 'sh' was called once with the given command
   *
   * @param expectedCommand The command that is expected
   */
  static assertOneShellCommand(String expectedCommand) {
    String actualCommand = assertOnce(StepConstants.SH)
    assertEquals(expectedCommand, actualCommand)
  }

}
