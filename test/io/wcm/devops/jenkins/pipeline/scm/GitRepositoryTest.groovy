/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 - 2018 wcm.io DevOps
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
package io.wcm.devops.jenkins.pipeline.scm

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.CpsScriptTestBase
import org.junit.Test
import static org.junit.Assert.*

class GitRepositoryTest extends CpsScriptTestBase {

  GitRepository underTest

  @Test
  void shouldParseSshVariant1() {
    String url = "git@github.com:wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals(null, underTest.protocolPrefix)
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
    assertEquals("git", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseSshVariant2() {
    String url = "git1@subsubdomain.subdomain.do-main.tld:group-name/PRO-ject.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("group-name", underTest.getGroup())
    assertEquals("PRO-ject.git", underTest.getProject())
    assertEquals("PRO-ject", underTest.getProjectName())
    assertEquals("subsubdomain.subdomain.do-main.tld", underTest.getServer())
    assertEquals("git1", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseSshVariant3() {
    String url = "git2@subsubdomain.subdomain.do_main.tld:group_name/PRO_ject.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals(null, underTest.protocolPrefix)
    assertEquals("group_name", underTest.getGroup())
    assertEquals("PRO_ject.git", underTest.getProject())
    assertEquals("PRO_ject", underTest.getProjectName())
    assertEquals("subsubdomain.subdomain.do_main.tld", underTest.getServer())
    assertEquals("git2", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseSshWithSubGroup() {
    String url = "git-4@github.com:wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals(null, underTest.protocolPrefix)
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
    assertEquals("git-4", underTest.getUsername())
  }

  @Test
  void shouldParseSshWithPort() {
    String url = "ssh://customusername-_1234@github.com:22/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("ssh://", underTest.protocolPrefix)
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com:22", underTest.getServer())
    assertEquals("customusername-_1234", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseHttpsUrl() {
    String url = "https://github.com/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertTrue(underTest.isHttps())
    assertEquals("https://", underTest.protocolPrefix)
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
    assertNull(underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseHttpUrlWithSubgroup() {
    String url = "http://myusername@github.com/wcm-io-devops/jenkins/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertTrue(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("http://", underTest.protocolPrefix)
    assertEquals("wcm-io-devops/jenkins", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
    assertEquals("myusername", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldParseHttpsUrlWithPort() {
    String url = "https://username1@github.com:443/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertTrue(underTest.isHttps())
    assertEquals("https://", underTest.protocolPrefix)
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com:443", underTest.getServer())
    assertEquals("username1", underTest.getUsername())
    assertEquals(url, underTest.getUrl())
  }

  @Test
  void shouldManiupulateHttpsUsername() {
    String url = "https://username1@github.com:443/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    underTest.setUsername("username2")
    assertEquals("https://username2@github.com:443/wcm-io-devops/jenkins-pipeline-library.git", underTest.getUrl())
    underTest.setUsername(null)
    assertEquals("https://github.com:443/wcm-io-devops/jenkins-pipeline-library.git", underTest.getUrl())
    underTest.setUsername("username1")
    assertEquals(url, underTest.getUrl())
  }

  @Test(expected = AbortException.class)
  void shouldThrowExceptionOnInvalidUrl() {
    underTest = new GitRepository(this.script, "")
  }

}
