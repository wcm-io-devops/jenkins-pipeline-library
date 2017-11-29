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
package vars.execManagedShellScript

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.SH
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertTwice
import static org.junit.Assert.assertEquals

class ExecManagedShellScriptIT extends LibraryIntegrationTestBase {

  @Test
  void shouldCallWithOneListItem() {
    loadAndExecuteScript("vars/execManagedShellScript/jobs/execMangedShellScriptVariant1Test.groovy")
    List<String> actualShellCalls = (List) assertTwice(SH)
    assertEquals("chmod +x /path/to/workspace@tmp/some-file-id-variant1", actualShellCalls.get(0))
    assertEquals([returnStdout: true, script: "/path/to/workspace@tmp/some-file-id-variant1 oneArg"], actualShellCalls.get(1))
  }

  @Test
  void shouldCallWithMultipleListItems() {
    loadAndExecuteScript("vars/execManagedShellScript/jobs/execMangedShellScriptVariant2Test.groovy")
    List<String> actualShellCalls = (List) assertTwice(SH)
    assertEquals("chmod +x /path/to/workspace@tmp/some-file-id-variant2", actualShellCalls.get(0))
    assertEquals([returnStdout: true, script: "/path/to/workspace@tmp/some-file-id-variant2 argOne argTwo argThree=value"], actualShellCalls.get(1))
  }

  @Test
  void shouldCallWithArgLine() {
    loadAndExecuteScript("vars/execManagedShellScript/jobs/execMangedShellScriptVariant3Test.groovy")
    List<String> actualShellCalls = (List) assertTwice(SH)
    assertEquals("chmod +x /path/to/workspace@tmp/some-file-id-variant3", actualShellCalls.get(0))
    assertEquals([returnStdout: true, script: "/path/to/workspace@tmp/some-file-id-variant3 customArgLine prop1=value1 -Dtest"], actualShellCalls.get(1))
  }

}
