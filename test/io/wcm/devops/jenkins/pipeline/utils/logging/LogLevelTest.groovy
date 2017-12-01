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

import org.junit.Test

import static org.junit.Assert.assertEquals

class LogLevelTest {

  @Test
  void shouldReturnDefaultLogLevel() {
    assertEquals(LogLevel.INFO, LogLevel.fromInteger(-1))
    assertEquals(LogLevel.INFO, LogLevel.fromInteger(9546131))
    assertEquals(LogLevel.INFO, LogLevel.fromString("unknown"))
  }

  @Test
  void shouldReturnCorrectValue() {
    assertEquals(0, LogLevel.ALL.getLevel())
    assertEquals(4, LogLevel.INFO.getLevel())
    assertEquals(Integer.MAX_VALUE, LogLevel.NONE.getLevel())
  }

}
