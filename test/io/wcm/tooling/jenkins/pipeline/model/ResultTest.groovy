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
package io.wcm.tooling.jenkins.pipeline.model

import hudson.model.Result as HudsonResult
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ResultTest {

  @Test
  void shouldReturnDefault() {
    assertEquals(Result.FAILURE, Result.fromString("not found"))
  }

  @Test
  void shouldReturnExistingHudsonResults() {
    assertEquals(Result.NOT_BUILD, Result.fromString(HudsonResult.NOT_BUILT.toString()))
    assertEquals(Result.ABORTED, Result.fromString(HudsonResult.ABORTED.toString()))
    assertEquals(Result.FAILURE, Result.fromString(HudsonResult.FAILURE.toString()))
    assertEquals(Result.UNSTABLE, Result.fromString(HudsonResult.UNSTABLE.toString()))
    assertEquals(Result.SUCCESS, Result.fromString(HudsonResult.SUCCESS.toString()))
  }

  @Test
  void shouldReturnCustomResults() {
    assertEquals(Result.STILL_UNSTABLE, Result.fromString(Result.STILL_UNSTABLE.toString()))
    assertEquals(Result.STILL_FAILING, Result.fromString(Result.STILL_FAILING.toString()))
    assertEquals(Result.FIXED, Result.fromString(Result.FIXED.toString()))
  }

  @Test
  void shouldBeBetterThan() {
    assertBetter(Result.NOT_BUILD, Result.ABORTED)

    assertBetter(Result.FAILURE, Result.NOT_BUILD)
    assertBetter(Result.STILL_FAILING, Result.NOT_BUILD)

    assertBetter(Result.UNSTABLE, Result.FAILURE)
    assertBetter(Result.UNSTABLE, Result.STILL_FAILING)
    assertBetter(Result.STILL_UNSTABLE, Result.FAILURE)

    assertBetter(Result.SUCCESS, Result.UNSTABLE)
    assertBetter(Result.FIXED, Result.UNSTABLE)
  }

  @Test
  void shouldBeBetterWorseThan() {
    assertWorseThan(Result.ABORTED, Result.NOT_BUILD)

    assertWorseThan(Result.NOT_BUILD, Result.FAILURE)
    assertWorseThan(Result.NOT_BUILD, Result.STILL_FAILING)

    assertWorseThan(Result.FAILURE, Result.UNSTABLE)
    assertWorseThan(Result.STILL_FAILING, Result.UNSTABLE)
    assertWorseThan(Result.FAILURE, Result.STILL_UNSTABLE)

    assertWorseThan(Result.UNSTABLE, Result.SUCCESS)
  }

  @Test
  void shouldBeBetterThanOrEqual() {
    assertBetterOrEqual(Result.NOT_BUILD, Result.NOT_BUILD)
    assertBetterOrEqual(Result.NOT_BUILD, Result.ABORTED)

    assertBetterOrEqual(Result.FAILURE, Result.FAILURE)
    assertBetterOrEqual(Result.FAILURE, Result.NOT_BUILD)

    assertBetterOrEqual(Result.UNSTABLE, Result.UNSTABLE)
    assertBetterOrEqual(Result.UNSTABLE, Result.STILL_UNSTABLE)
    assertBetterOrEqual(Result.UNSTABLE, Result.FAILURE)


    assertBetterOrEqual(Result.STILL_UNSTABLE, Result.FAILURE)

    assertBetterOrEqual(Result.SUCCESS, Result.UNSTABLE)
    assertBetterOrEqual(Result.SUCCESS, Result.SUCCESS)
    assertBetterOrEqual(Result.FIXED, Result.SUCCESS)
  }

  @Test
  void shouldBeBetterWorseThanOrEqual() {
    assertWorseOrEqual(Result.ABORTED, Result.NOT_BUILD)

    assertWorseOrEqual(Result.NOT_BUILD, Result.FAILURE)
    assertWorseOrEqual(Result.NOT_BUILD, Result.STILL_FAILING)

    assertWorseOrEqual(Result.FAILURE, Result.UNSTABLE)
    assertWorseOrEqual(Result.STILL_FAILING, Result.UNSTABLE)
    assertWorseOrEqual(Result.FAILURE, Result.STILL_UNSTABLE)

    assertWorseOrEqual(Result.UNSTABLE, Result.SUCCESS)

    assertWorseOrEqual(Result.UNSTABLE, Result.STILL_UNSTABLE)
    assertWorseOrEqual(Result.STILL_FAILING, Result.STILL_UNSTABLE)
    assertWorseOrEqual(Result.STILL_FAILING, Result.FIXED)
    assertWorseOrEqual(Result.STILL_FAILING, Result.FAILURE)
  }

  protected assertBetter(Result that, Result than) {
    assertTrue("'$that' should be better than '$than'", that.isBetterThan(than))
  }

  protected assertWorseThan(Result that, Result than) {
    assertTrue("'$that' should be worse than '$than'", that.isWorseThan(than))
  }

  protected assertBetterOrEqual(Result that, Result than) {
    assertTrue("'$that' should be better or equal than '$than'", that.isBetterOrEqualTo(than))
  }

  protected assertWorseOrEqual(Result that, Result than) {
    assertTrue("'$that' should be worse or equal than '$than'", that.isWorseOrEqualTo(than))
  }
}
