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
package io.wcm.devops.jenkins.pipeline.environment

import io.wcm.testing.jenkins.pipeline.CpsScriptTestBase
import org.junit.Assert
import org.junit.Test

class EnvironmentUtilsTest extends CpsScriptTestBase {

  EnvironmentUtils underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    underTest = new EnvironmentUtils(this.script)
  }

  @Test
  void shouldSetNotExistingVariable() {
    this.script.setEnv(EnvironmentConstants.GIT_BRANCH, null)
    Boolean result = underTest.setEnvWhenEmpty(EnvironmentConstants.GIT_BRANCH, "newvalue")
    Assert.assertEquals("newvalue",this.script.getEnv(EnvironmentConstants.GIT_BRANCH))
    Assert.assertTrue(result)
  }

  @Test
  void shouldSetEmptyVariable() {
    this.script.setEnv(EnvironmentConstants.GIT_BRANCH, "")
    Boolean result = underTest.setEnvWhenEmpty(EnvironmentConstants.GIT_BRANCH, "newvalue")
    Assert.assertEquals("newvalue",this.script.getEnv(EnvironmentConstants.GIT_BRANCH))
    Assert.assertTrue(result)
  }

  @Test
  void shouldNotOverwriteExistingVariable() {
    this.script.setEnv(EnvironmentConstants.GIT_BRANCH, "existing")
    Boolean result = underTest.setEnvWhenEmpty(EnvironmentConstants.GIT_BRANCH, "newvalue")
    Assert.assertEquals("existing",this.script.getEnv(EnvironmentConstants.GIT_BRANCH))
    Assert.assertFalse(result)
  }
}
