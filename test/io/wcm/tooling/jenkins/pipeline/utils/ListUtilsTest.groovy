package io.wcm.tooling.jenkins.pipeline.utils

import org.junit.Assert
import org.junit.Test

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

class ListUtilsTest {

  @Test
  void shouldRemoveMiddleItem() {
    List underTest = [1, 2, 3]
    underTest = ListUtils.removeAt(underTest, 1)
    Assert.assertEquals([1, 3], underTest)
  }

  @Test
  void shouldRemoveFirstItem() {
    List underTest = [1, 2, 3]
    underTest = ListUtils.removeAt(underTest, 0)
    Assert.assertEquals([2, 3], underTest)
  }

  @Test
  void shouldRemoveLastItem() {
    List underTest = [1, 2, 3]
    underTest = ListUtils.removeAt(underTest, 2)
    Assert.assertEquals([1, 2], underTest)
  }

  @Test
  void shouldNotFailOnOutOfBoundsIndex() {
    List underTest = [1, 2, 3]
    underTest = ListUtils.removeAt(underTest, -1)
    Assert.assertEquals([1, 2, 3], underTest)
    underTest = ListUtils.removeAt(underTest, 4)
    Assert.assertEquals([1, 2, 3], underTest)
  }

}
