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
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
  }

  @Test
  void shouldParseSshVariant2() {
    String url = "git@subsubdomain.subdomain.do-main.tld:group-name/PRO-ject.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("group-name", underTest.getGroup())
    assertEquals("PRO-ject.git", underTest.getProject())
    assertEquals("PRO-ject", underTest.getProjectName())
    assertEquals("subsubdomain.subdomain.do-main.tld", underTest.getServer())
  }

  @Test
  void shouldParseSshVariant3() {
    String url = "git@subsubdomain.subdomain.do_main.tld:group_name/PRO_ject.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("group_name", underTest.getGroup())
    assertEquals("PRO_ject.git", underTest.getProject())
    assertEquals("PRO_ject", underTest.getProjectName())
    assertEquals("subsubdomain.subdomain.do_main.tld", underTest.getServer())
  }

  @Test
  void shouldParseSshWithSubGroup() {
    String url = "git@github.com:wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
  }

  @Test
  void shouldParseSshWithPort() {
    String url = "ssh://git@github.com:22/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertTrue(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com:22", underTest.getServer())
  }

  @Test
  void shouldParseHttpsUrl() {
    String url = "https://github.com/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertTrue(underTest.isHttps())
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
  }

  @Test
  void shouldParseHttpUrlWithSubgroup() {
    String url = "http://github.com/wcm-io-devops/jenkins/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertTrue(underTest.isHttp())
    assertFalse(underTest.isHttps())
    assertEquals("wcm-io-devops/jenkins", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com", underTest.getServer())
  }

  @Test
  void shouldParseHttpsUrlWithPort() {
    String url = "https://github.com:443/wcm-io-devops/jenkins-pipeline-library.git"
    underTest = new GitRepository(this.script, url)
    assertEquals(url, underTest.getUrl())
    assertTrue(underTest.isValid())
    assertFalse(underTest.isSsh())
    assertFalse(underTest.isHttp())
    assertTrue(underTest.isHttps())
    assertEquals("wcm-io-devops", underTest.getGroup())
    assertEquals("jenkins-pipeline-library.git", underTest.getProject())
    assertEquals("jenkins-pipeline-library", underTest.getProjectName())
    assertEquals("github.com:443", underTest.getServer())
  }

  @Test(expected = AbortException.class)
  void shouldThrowExceptionOnInvalidUrl() {
    underTest = new GitRepository(this.script, "")
  }

}
