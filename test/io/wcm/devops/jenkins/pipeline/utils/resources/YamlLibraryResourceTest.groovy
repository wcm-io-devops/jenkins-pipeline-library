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
package io.wcm.devops.jenkins.pipeline.utils.resources

import hudson.AbortException
import io.wcm.testing.jenkins.pipeline.DSLTestBase
import org.junit.Test

import static org.junit.Assert.assertEquals

class YamlLibraryResourceTest extends DSLTestBase {

  YamlLibraryResource underTest

  @Test
  void shouldLoadExistingYamlResource() {
    underTest = new YamlLibraryResource(this.dslMock.getMock(), 'yaml/valid.yaml')
    Object expected = [
      foo  : "bar",
      list1: [
        "entry1",
        "entry2",
        "entry3",
      ],
      list2: [
        [
          entry1: [
            key: "value1"
          ],
        ],
        [
          entry2: [
            key: "value2"
          ],
        ],
        [
          entry3: [
            key: "value3"
          ]
        ]
      ]
    ]
    Object actual = underTest.load()
    assertEquals(expected, actual)
  }

  @Test(expected = AbortException.class)
  void shouldFailOnNonExistingJsonResource() {
    underTest = new YamlLibraryResource(this.dslMock.getMock(), 'yaml/not-existing.yaml')
    underTest.load()
  }
}
