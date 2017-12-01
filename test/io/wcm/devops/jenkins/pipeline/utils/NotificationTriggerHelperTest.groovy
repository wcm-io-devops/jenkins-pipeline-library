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
package io.wcm.devops.jenkins.pipeline.utils

import hudson.model.Result as HudsonResult
import io.wcm.devops.jenkins.pipeline.model.Result
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class NotificationTriggerHelperTest {

  NotificationTriggerHelper underTest

  @Before
  void setUp() {

  }

  @Test
  void shouldReturnCurrentResultForSuccess() {
    underTest = new NotificationTriggerHelper(HudsonResult.SUCCESS)
    assertEquals(Result.SUCCESS, underTest.getTrigger())

    assertTrue(underTest.isSuccess())
    assertFalse(underTest.isFailure())
    assertFalse(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertFalse(underTest.isStillFailing())
    assertFalse(underTest.isUnstable())
    assertFalse(underTest.isStillUnstable())
  }

  @Test
  void shouldReturnCurrentResultForFailure() {
    underTest = new NotificationTriggerHelper(HudsonResult.FAILURE)
    assertEquals(Result.FAILURE, underTest.getTrigger())

    assertFalse(underTest.isSuccess())
    assertTrue(underTest.isFailure())
    assertFalse(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertFalse(underTest.isStillFailing())
    assertFalse(underTest.isUnstable())
    assertFalse(underTest.isStillUnstable())
  }

  @Test
  void shouldReturnCurrentResultForUnstable() {
    underTest = new NotificationTriggerHelper(HudsonResult.UNSTABLE, null)
    assertEquals(Result.UNSTABLE, underTest.getTrigger())

    assertFalse(underTest.isSuccess())
    assertFalse(underTest.isFailure())
    assertFalse(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertFalse(underTest.isStillFailing())
    assertTrue(underTest.isUnstable())
    assertFalse(underTest.isStillUnstable())
  }

  @Test
  void shouldReturnCurrentResultForAbort() {
    underTest = new NotificationTriggerHelper(HudsonResult.ABORTED, null)
    assertEquals(Result.ABORTED, underTest.getTrigger())

    assertFalse(underTest.isSuccess())
    assertFalse(underTest.isFailure())
    assertTrue(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertFalse(underTest.isStillFailing())
    assertFalse(underTest.isUnstable())
    assertFalse(underTest.isStillUnstable())
  }

  @Test
  void shouldReturnStillUnstable() {
    underTest = new NotificationTriggerHelper(Result.UNSTABLE.toString(), Result.UNSTABLE.toString())
    assertEquals(Result.STILL_UNSTABLE, underTest.getTrigger())

    assertFalse(underTest.isSuccess())
    assertFalse(underTest.isFailure())
    assertFalse(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertFalse(underTest.isStillFailing())
    assertFalse(underTest.isUnstable())
    assertTrue(underTest.isStillUnstable())
  }

  @Test
  void shouldReturnStillFailing() {
    underTest = new NotificationTriggerHelper(HudsonResult.FAILURE.toString(), HudsonResult.FAILURE.toString())
    assertEquals(Result.STILL_FAILING, underTest.getTrigger())

    assertFalse(underTest.isSuccess())
    assertFalse(underTest.isFailure())
    assertFalse(underTest.isAborted())
    assertFalse(underTest.isFixed())
    assertTrue(underTest.isStillFailing())
    assertFalse(underTest.isUnstable())
    assertFalse(underTest.isStillUnstable())
  }

  @Test
  void shouldReplaceOneOccurrence() {
    underTest = new NotificationTriggerHelper(HudsonResult.FAILURE.toString(), HudsonResult.FAILURE.toString())
    assertEquals('-STILL FAILING_', underTest.replaceEnvVar('-${NOTIFICATION_TRIGGER}_', underTest.getTrigger().toString()))
  }

  @Test
  void shouldReplaceMultipleOccurrences() {
    underTest = new NotificationTriggerHelper(HudsonResult.FAILURE, HudsonResult.FAILURE)
    assertEquals('-STILL FAILING_loremSTILL FAILING', underTest.replaceEnvVar('-${NOTIFICATION_TRIGGER}_lorem${NOTIFICATION_TRIGGER}', underTest.getTrigger().toString()))
  }

  @Test
  void shouldReturnFixed() {
    assertFixed(HudsonResult.SUCCESS.toString(), HudsonResult.UNSTABLE.toString())
    assertFixed(HudsonResult.SUCCESS.toString(), HudsonResult.FAILURE.toString())
  }

  @Test
  void shouldNotReturnFixed() {
    assertNotFixed(HudsonResult.SUCCESS.toString(), HudsonResult.ABORTED.toString())
    assertNotFixed(HudsonResult.SUCCESS.toString(), HudsonResult.NOT_BUILT.toString())

    assertNotFixed(HudsonResult.UNSTABLE.toString(), HudsonResult.ABORTED.toString())
    assertNotFixed(HudsonResult.ABORTED.toString(), HudsonResult.NOT_BUILT.toString())
  }

  @Test
  void shouldReturnSuccessPerDefault() {
    underTest = new NotificationTriggerHelper((String) null, null)
    assertEquals(Result.SUCCESS, underTest.getTrigger())
  }

  protected assertFixed(String currentResult, String lastResult) {
    underTest = new NotificationTriggerHelper(currentResult, lastResult)
    assertEquals(String.format("expected fixed when result is changing from '%s' to %s", currentResult, lastResult), Result.FIXED, underTest.getTrigger())

    assertFalse(String.format("'isSuccess' should return false when result is changing from '%s' to '%s'", lastResult, currentResult), underTest.isSuccess())
    assertFalse(String.format("'isFailure' should return false when result is changing from '%s' to '%s'", lastResult, currentResult), underTest.isFailure())
    assertFalse(String.format("'isAborted' should return false when result is changing from '%s' to '%s'", lastResult, currentResult), underTest.isAborted())
    assertTrue(String.format("'isFixed' should return true when result is changing from '%s' to %s", lastResult, currentResult), underTest.isFixed())
    assertFalse(String.format("'isStillFailing' should return false when result is changing from '%s' to %s", lastResult, currentResult), underTest.isStillFailing())
    assertFalse(String.format("'isUnstable' should return false when result is changing from '%s' to %s", lastResult, currentResult), underTest.isUnstable())
    assertFalse(String.format("'isStillUnstable' should return false when result is changing from '%s' to %s", lastResult, currentResult), underTest.isStillUnstable())
  }

  protected assertNotFixed(String currentResult, String lastResult) {
    underTest = new NotificationTriggerHelper(currentResult, lastResult)
    assertNotEquals(String.format("expected not fixed when result is changing from '%s' to %s", lastResult, currentResult), Result.FIXED, underTest.getTrigger())
  }
}
