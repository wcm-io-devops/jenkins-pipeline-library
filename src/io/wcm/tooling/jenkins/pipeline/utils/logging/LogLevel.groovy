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
package io.wcm.tooling.jenkins.pipeline.utils.logging

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/**
 * Enumeration for log levels
 */
@SuppressFBWarnings('ME_ENUM_FIELD_SETTER')
enum LogLevel implements Serializable {

  ALL(0, 0),
  TRACE(2, 8),
  DEBUG(3, 12),
  INFO(4, 0),
  WARN(5, 202),
  ERROR(6, 5),
  FATAL(7, 9),
  NONE(Integer.MAX_VALUE, 0)

  Integer level

  static COLOR_CODE_PREFIX = "1;38;5;"

  Integer color

  private static final long serialVersionUID = 1L

  LogLevel(Integer level, Integer color) {
    this.level = level
    this.color = color
  }

  @NonCPS
  static LogLevel fromInteger(Integer value) {
    for (lvl in values()) {
      if (lvl.getLevel() == value) return lvl
    }
    return INFO
  }

  @NonCPS
  static LogLevel fromString(String value) {
    for (lvl in values()) {
      if (lvl.toString().equalsIgnoreCase(value)) return lvl
    }
    return INFO
  }

  @NonCPS
  public String getColorCode() {
    return COLOR_CODE_PREFIX + color.toString()
  }


}
