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
package vars.checkoutScm

import io.wcm.testing.jenkins.pipeline.LibraryIntegrationTestBase
import io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants
import org.junit.Test

import static io.wcm.testing.jenkins.pipeline.StepConstants.CHECKOUT
import static io.wcm.testing.jenkins.pipeline.recorder.StepRecorderAssert.assertOnce
import static org.junit.Assert.assertEquals

class CheckoutScmIT extends LibraryIntegrationTestBase {

  @Override
  void setUp() throws Exception {
    super.setUp()
  }

  @Test
  void shouldUseDefaultConfiguration() {
    loadAndExecuteScript("vars/checkoutScm/jobs/checkoutScmDefaultsJob.groovy")
    Map scmCheckoutCall = (Map) assertOnce(CHECKOUT)

    assertEquals("Used SCM should be git", "GitSCM", scmCheckoutCall.get('$class'))
    assertEquals("doGenerateSubmoduleConfigurations should be false", false, scmCheckoutCall.get("doGenerateSubmoduleConfigurations"))
    assertEquals("'extensions' should have LocalBranch extension as default", [[$class: 'LocalBranch']], scmCheckoutCall.get("extensions"))
    assertEquals("'submoduleCfg' should be empty", 0, scmCheckoutCall.get("submoduleCfg").size())

    // test branches
    List branches = scmCheckoutCall.get("branches")
    assertEquals("[[name:*/master], [name:*/develop]]", branches.toString())

    List userRemoteConfigs = (List) scmCheckoutCall.get("userRemoteConfigs")
    assertEquals("One userRemoteConfig expected", 1, userRemoteConfigs.size())
    Map userRemoteConfig = (Map) userRemoteConfigs.get(0)
    // check for credential auto detection
    assertEquals("ssh-git-credentials-id", userRemoteConfig.get("credentialsId"))
    // check for correct url
    assertEquals("git@git-ssh.domain.tld/group/project1.git", userRemoteConfig.get("url"))
  }

  @Test
  void shouldUseCustomConfiguration() {
    loadAndExecuteScript("vars/checkoutScm/jobs/checkoutScmCustomVariant1Job.groovy")
    Map scmCheckoutCall = (Map) assertOnce(CHECKOUT)

    assertEquals("Used SCM should be git", "GitSCM", scmCheckoutCall.get('$class'))

    // test sub module configuration
    assertEquals("doGenerateSubmoduleConfigurations should be true", true, scmCheckoutCall.get("doGenerateSubmoduleConfigurations"))

    // test extensions
    List actualExtensions = (List) scmCheckoutCall.get("extensions")
    List actualSubmoduleCfg = (List) scmCheckoutCall.get("submoduleCfg")
    assertEquals('[[$class:CleanBeforeCheckout], [$class:CloneOption, depth:0, noTags:false, reference:, shallow:true]]', actualExtensions.toString())
    assertEquals('[[$class:CustomSubModuleCfg]]', actualSubmoduleCfg.toString())

    // test branches
    List branches = (List) scmCheckoutCall.get("branches")
    assertEquals("[[name:customBranch]]", branches.toString())

    List userRemoteConfigs = (List) scmCheckoutCall.get("userRemoteConfigs")
    assertEquals("One userRemoteConfig expected", 1, userRemoteConfigs.size())
    Map userRemoteConfig = (Map) userRemoteConfigs.get(0)
    // check for credential auto detection
    assertEquals("CUSTOM_CREDENTIAL_ID", userRemoteConfig.get("credentialsId"))
    // check for correct url
    assertEquals("git@git-ssh.betterdomain.tld/group/project1.git", userRemoteConfig.get("url"))
  }

  @Test
  void shouldUsePassedUserRemoteConfigs() {
    loadAndExecuteScript("vars/checkoutScm/jobs/checkoutScmCustomVariant2Job.groovy")
    Map scmCheckoutCall = (Map) assertOnce(CHECKOUT)
    assertEquals("Used SCM should be git", "GitSCM", scmCheckoutCall.get('$class'))

    List userRemoteConfigs = (List) scmCheckoutCall.get("userRemoteConfigs")
    assertEquals("One userRemoteConfig expected", 1, userRemoteConfigs.size())
    Map userRemoteConfig = (Map) userRemoteConfigs.get(0)
    // check for credential auto detection
    assertEquals("USER_REMOTE_CONFIGS_CREDENTIAL", userRemoteConfig.get("credentialsId"))
    // check for correct url
    assertEquals("USER_REMOTE_CONFIGS_URL", userRemoteConfig.get("url"))
  }

  @Test
  void shouldUsePassedUserRemoteConfig() {
    loadAndExecuteScript("vars/checkoutScm/jobs/checkoutScmCustomVariant3Job.groovy")
    Map scmCheckoutCall = (Map) assertOnce(CHECKOUT)
    assertEquals("Used SCM should be git", "GitSCM", scmCheckoutCall.get('$class'))

    List userRemoteConfigs = (List) scmCheckoutCall.get("userRemoteConfigs")
    assertEquals("One userRemoteConfig expected", 1, userRemoteConfigs.size())
    Map userRemoteConfig = (Map) userRemoteConfigs.get(0)
    // check for credential auto detection
    assertEquals("USER_REMOTE_CONFIG_CREDENTIAL", userRemoteConfig.get("credentialsId"))
    // check for correct url
    assertEquals("USER_REMOTE_CONFIG_URL", userRemoteConfig.get("url"))
  }

  @Test
  void shouldCheckoutWithEmptyCredentials() {
    loadAndExecuteScript("vars/checkoutScm/jobs/checkoutScmEmptyCredentialsJob.groovy")
    Map scmCheckoutCall = (Map) assertOnce(CHECKOUT)

    assertEquals("Used SCM should be git", "GitSCM", scmCheckoutCall.get('$class'))
    assertEquals("doGenerateSubmoduleConfigurations should be false", false, scmCheckoutCall.get("doGenerateSubmoduleConfigurations"))
    assertEquals("'extensions' should have LocalBranch extension as default", [[$class: 'LocalBranch']], scmCheckoutCall.get("extensions"))
    assertEquals("'submoduleCfg' should be empty", 0, scmCheckoutCall.get("submoduleCfg").size())

    // test branches
    List branches = scmCheckoutCall.get("branches")
    assertEquals("[[name:*/master], [name:*/develop]]", branches.toString())

    List userRemoteConfigs = (List) scmCheckoutCall.get("userRemoteConfigs")
    assertEquals("One userRemoteConfig expected", 1, userRemoteConfigs.size())
    Map userRemoteConfig = (Map) userRemoteConfigs.get(0)

    // check for empty credentails
    assertEquals("noCredentialsIdFound", userRemoteConfig[ConfigConstants.SCM_CREDENTIALS_ID] ?: "noCredentialsIdFound")
    // check for correct url
    assertEquals("git@unknowndomain.tld/group/project1.git", userRemoteConfig.get("url"))
  }


}
