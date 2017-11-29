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

package io.wcm.tooling.jenkins.pipeline.tools.ansible

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Test

import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*
import static org.junit.Assert.*

class RoleRequirementsTest extends DSLTestBase {

  private RoleRequirements underTest

  @Override
  void setUp() throws Exception {
    super.setUp()
    List<Object> ymlContent = dslMock.readYaml("tools/ansible/requirements.yml")
    underTest = new RoleRequirements(ymlContent)
  }

  @Test
  void shouldParseRoles() {
    assertEquals(4, underTest.getRoles().size())
    List roles = underTest.getRoles()

    // test galaxy role without version
    Role role1 = roles.get(0)
    assertTrue(role1.isGalaxyRole())
    assertFalse(role1.isScmRole())
    assertEquals("williamyeh.oracle-java", role1.getSrc())
    assertEquals("williamyeh.oracle-java", role1.getName())
    assertEquals(null, role1.getScm())
    assertEquals("master", role1.getVersion())

    // test galaxy role with version
    Role role2 = roles.get(1)
    assertTrue(role2.isGalaxyRole())
    assertFalse(role2.isScmRole())
    assertEquals("tecris.maven", role2.getSrc())
    assertEquals("tecris.maven", role2.getName())
    assertEquals(null, role2.getScm())
    assertEquals("v3.5.2", role2.getVersion())

    // test scm role without version
    Role role3 = roles.get(2)
    assertFalse(role3.isGalaxyRole())
    assertTrue(role3.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-aem-cms.git", role3.getSrc())
    assertEquals("aem-cms", role3.getName())
    assertEquals("git", role3.getScm())
    assertEquals("master", role3.getVersion())

    // test scm role without version
    Role role4 = roles.get(3)
    assertFalse(role4.isGalaxyRole())
    assertTrue(role4.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-aem-service.git", role4.getSrc())
    assertEquals("aem-service", role4.getName())
    assertEquals("git", role4.getScm())
    assertEquals("develop", role4.getVersion())
  }

  @Test
  void shouldCreateCorrectCheckoutConfigs() {
    List<Map> checkoutConfigs = underTest.getCheckoutConfigs()
    assertEquals(2, checkoutConfigs.size())

    Map expectedConfig1 = [
        (SCM): [
            (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-aem-cms.git",
            (SCM_BRANCHES)  : [[name: "master"]],
            (SCM_EXTENSIONS): [
                [$class: 'LocalBranch'],
                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'aem-cms'],
                [$class: 'ScmName', name: 'aem-cms']
            ],
        ]
    ]

    Map expectedConfig2 = [
        (SCM): [
            (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-aem-service.git",
            (SCM_BRANCHES)  : [[name: "develop"]],
            (SCM_EXTENSIONS): [
                [$class: 'LocalBranch'],
                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'aem-service'],
                [$class: 'ScmName', name: 'aem-service']
            ],
        ]
    ]

    assertEquals(expectedConfig1, checkoutConfigs.get(0))
    assertEquals(expectedConfig2, checkoutConfigs.get(1))
  }

}
