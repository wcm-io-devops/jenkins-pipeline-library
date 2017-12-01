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
package io.wcm.devops.jenkins.pipeline.utils.resources

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Assert
import org.junit.Test
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when

class LibraryResourceTest extends DSLTestBase {

  LibraryResource underTest

  @Test
  void shouldLoadExistingResource() {
    underTest = new LibraryResource(this.dslMock.getMock(), 'example-resource.txt')
    String actual = underTest.load()
    Assert.assertEquals("foobar", actual)
  }

  @Test(expected = AbortException.class)
  void shouldFailOnNonExistingResource() {
    underTest = new LibraryResource(this.dslMock.getMock(), 'notexisting.txt')
    underTest.load()
  }

  @Test
  void shouldReturnCachedResource() {
    underTest = new LibraryResource(this.dslMock.getMock(), 'example-resource.txt')
    // load the first time
    String actual1 = underTest.load()

    // return modified resouce on next call
    when(dslMock.getMock().invokeMethod(eq("libraryResource"), any())).then(new Answer<String>() {
      @Override
      String answer(InvocationOnMock invocationOnMock) throws Throwable {
        return "you should not see me"
      }
    })

    String actual2 = underTest.load()
    Assert.assertEquals("foobar", actual1)
    Assert.assertEquals("foobar", actual2)
  }
}
