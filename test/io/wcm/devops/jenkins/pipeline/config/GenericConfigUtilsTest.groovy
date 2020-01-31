/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2020 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.config

import io.wcm.testing.jenkins.pipeline.CpsScriptTestBase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GenericConfigUtilsTest extends CpsScriptTestBase {

  GenericConfigUtils underTest

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()
    underTest = new GenericConfigUtils(this.script)
    this.script.setEnv("GIT_BRANCH", "MOCKED_GIT_BRANCH")
    this.script.setEnv("JOB_NAME", "MOCKED_JOB_NAME")
  }

  @Test
  void shouldBuildCorrectFQJN() {
    Assert.assertEquals("MOCKED_JOB_NAME@MOCKED_GIT_BRANCH", underTest.getFQJN())

    this.script.setEnv("GIT_BRANCH", null)
    Assert.assertEquals("MOCKED_JOB_NAME", underTest.getFQJN())

    this.script.setEnv("GIT_BRANCH", "")
    Assert.assertEquals("MOCKED_JOB_NAME",underTest.getFQJN())

    this.script.setEnv("GIT_BRANCH", "origin/MOCKED_GIT_BRANCH")
    Assert.assertEquals("MOCKED_JOB_NAME@MOCKED_GIT_BRANCH",underTest.getFQJN())
  }
}
