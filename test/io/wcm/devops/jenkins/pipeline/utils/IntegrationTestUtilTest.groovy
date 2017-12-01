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

import org.junit.Assert
import org.junit.Test

class IntegrationTestUtilTest {

  @Test
  void shouldRecordTestResults() {
    IntegrationTestHelper.addTestPackage("package1")
    IntegrationTestHelper.addTestResult([name: "class1", exception: null])
    IntegrationTestHelper.addTestResult([name: "class2", exception: null])
    IntegrationTestHelper.addTestPackage("package2")
    IntegrationTestHelper.addTestResult([name: "class3", exception: null])
    IntegrationTestHelper.addTestResult([name: "class4", exception: null])

    Map results = IntegrationTestHelper.getResults()
    Assert.assertEquals([
        package1: [[name: "class1", exception: null], [name: "class2", exception: null]],
        package2: [[name: "class3", exception: null], [name: "class4", exception: null]],
    ], results)
  }

  @Test
  void shouldReset() {
    IntegrationTestHelper.addTestPackage("package1")
    IntegrationTestHelper.addTestResult([name: "class1", exception: null])
    IntegrationTestHelper.addTestResult([name: "class2", exception: null])
    IntegrationTestHelper.reset()

    Map results = IntegrationTestHelper.getResults()
    Assert.assertEquals([:], results)
  }

  @Test
  void shouldNotFailWithEmptyTests() {
    IntegrationTestHelper.addTestPackage("package1")
    Map results = IntegrationTestHelper.getResults()
    Assert.assertEquals([package1: []], results)
  }

}
