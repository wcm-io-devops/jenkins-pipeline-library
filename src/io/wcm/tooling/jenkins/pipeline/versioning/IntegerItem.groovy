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
package io.wcm.tooling.jenkins.pipeline.versioning

import com.cloudbees.groovy.cps.NonCPS

/**
 * Jenkins groovy sandbox compatible version of
 * https://github.com/apache/maven/blob/master/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ComparableVersion.java / IntegerItem
 */
class IntegerItem implements Item, Serializable {

  private static final long serialVersionUID = 1L

  public Integer value

  public static final Integer INTEGER_ZER0 = 0

  public static final IntegerItem ZERO = new IntegerItem()

  IntegerItem() {
    value = INTEGER_ZER0
  }

  IntegerItem(String str) {
    this.value = str.toInteger()
  }

  @Override
  @NonCPS
  int compareTo(Item item) {
    if (item == null) {
      return INTEGER_ZER0.equals(value) ? 0 : 1 // 1.0 == 1, 1.1 > 1
    }

    switch (item.getType()) {
      case INTEGER_ITEM:
        return value.compareTo(((IntegerItem) item).value)

      case STRING_ITEM:
        return 1 // 1.1 > 1-sp

      case LIST_ITEM:
        return 1 // 1.1 > 1-1

      default:
        throw new RuntimeException("invalid item: " + item.getClass())
    }
  }

  @Override
  @NonCPS
  int getType() {
    return INTEGER_ITEM
  }

  @Override
  @NonCPS
  boolean isNull() {
    return INTEGER_ZER0 == value
  }

  @NonCPS
  String toString() {
    return value.toString()
  }
}
