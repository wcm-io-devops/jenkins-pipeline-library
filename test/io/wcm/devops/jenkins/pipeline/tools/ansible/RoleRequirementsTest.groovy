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

package io.wcm.devops.jenkins.pipeline.tools.ansible

import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Test

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*
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
    assertEquals(7, underTest.getRoles().size())
    List roles = underTest.getRoles()

    // test galaxy role without version
    Role role0 = roles.get(0)
    assertTrue(role0.isGalaxyRole())
    assertFalse(role0.isScmRole())
    assertEquals("wcm_io_devops.jenkins_pipeline_library", role0.getSrc())
    assertEquals("wcm_io_devops.jenkins_pipeline_library", role0.getName())
    assertEquals(null, role0.getScm())
    assertEquals("master", role0.getVersion())

    // test galaxy role with name instead of src
    Role role1 = roles.get(1)
    assertTrue(role1.isGalaxyRole())
    assertFalse(role1.isScmRole())
    assertEquals("wcm_io_devops.jenkins_facts", role1.getSrc())
    assertEquals("wcm_io_devops.jenkins_facts", role1.getName())
    assertEquals(null, role1.getScm())
    assertEquals("master", role1.getVersion())

    // test galaxy role with version
    Role role2 = roles.get(2)
    assertTrue(role2.isGalaxyRole())
    assertFalse(role2.isScmRole())
    assertEquals("wcm_io_devops.jenkins_plugins", role2.getSrc())
    assertEquals("wcm_io_devops.jenkins_plugins", role2.getName())
    assertEquals(null, role2.getScm())
    assertEquals("1.2.0", role2.getVersion())

    // test scm role without version
    Role role3 = roles.get(3)
    assertFalse(role3.isGalaxyRole())
    assertTrue(role3.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-aem-cms.git", role3.getSrc())
    assertEquals("aem-cms", role3.getName())
    assertEquals("git", role3.getScm())
    assertEquals("master", role3.getVersion())

    // test scm role without version
    Role role4 = roles.get(4)
    assertFalse(role4.isGalaxyRole())
    assertTrue(role4.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-aem-service.git", role4.getSrc())
    assertEquals("aem-service", role4.getName())
    assertEquals("git", role4.getScm())
    assertEquals("develop", role4.getVersion())

    // test scm role with feature branch
    Role role5 = roles.get(5)
    assertFalse(role5.isGalaxyRole())
    assertTrue(role5.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-conga-aem-smoke-test.git", role5.getSrc())
    assertEquals("wcm_io_devops.conga_aem_smoke_test", role5.getName())
    assertEquals("git", role5.getScm())
    assertEquals("feature/debug-output", role5.getVersion())

    // test scm role with tag
    Role role6 = roles.get(6)
    assertFalse(role6.isGalaxyRole())
    assertTrue(role6.isScmRole())
    assertEquals("https://github.com/wcm-io-devops/ansible-aem-dispatcher-flush.git", role6.getSrc())
    assertEquals("wcm_io_devops.aem_dispatcher_flush", role6.getName())
    assertEquals("git", role6.getScm())
    assertEquals("1.0.0", role6.getVersion())
  }

  @Test
  void shouldCreateCorrectCheckoutConfigs() {
    List<Map> checkoutConfigs = underTest.getCheckoutConfigs()
    assertEquals(4, checkoutConfigs.size())

    Map expectedConfig1 = [
        (SCM): [
            (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-aem-cms.git",
            (SCM_BRANCHES)  : [[name: "*/master"]],
            (SCM_EXTENSIONS): [
                [$class: 'LocalBranch'],
                [$class: 'RelativeTargetDirectory', relativeTargetDir: '.roleRequirements/aem-cms'],
                [$class: 'ScmName', name: 'aem-cms']
            ],
        ]
    ]

    Map expectedConfig2 = [
        (SCM): [
            (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-aem-service.git",
            (SCM_BRANCHES)  : [[name: "*/develop"]],
            (SCM_EXTENSIONS): [
                [$class: 'LocalBranch'],
                [$class: 'RelativeTargetDirectory', relativeTargetDir: '.roleRequirements/aem-service'],
                [$class: 'ScmName', name: 'aem-service']
            ],
        ]
    ]

    Map expectedConfig3 = [
      (SCM): [
        (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-conga-aem-smoke-test.git",
        (SCM_BRANCHES)  : [[name: "*/feature/debug-output"]],
        (SCM_EXTENSIONS): [
          [$class: 'LocalBranch'],
          [$class: 'RelativeTargetDirectory', relativeTargetDir: '.roleRequirements/wcm_io_devops.conga_aem_smoke_test'],
          [$class: 'ScmName', name: 'wcm_io_devops.conga_aem_smoke_test']
        ],
      ]
    ]

    Map expectedConfig4 = [
      (SCM): [
        (SCM_URL)       : "https://github.com/wcm-io-devops/ansible-aem-dispatcher-flush.git",
        (SCM_BRANCHES)  : [[name: "1.0.0"]],
        (SCM_EXTENSIONS): [
          [$class: 'LocalBranch'],
          [$class: 'RelativeTargetDirectory', relativeTargetDir: '.roleRequirements/wcm_io_devops.aem_dispatcher_flush'],
          [$class: 'ScmName', name: 'wcm_io_devops.aem_dispatcher_flush']
        ],
      ]
    ]

    assertEquals(expectedConfig1, checkoutConfigs.get(0))
    assertEquals(expectedConfig2, checkoutConfigs.get(1))
    assertEquals(expectedConfig3, checkoutConfigs.get(2))
    assertEquals(expectedConfig4, checkoutConfigs.get(3))
  }

}
