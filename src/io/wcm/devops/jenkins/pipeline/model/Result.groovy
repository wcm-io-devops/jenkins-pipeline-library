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
package io.wcm.devops.jenkins.pipeline.model

import com.cloudbees.groovy.cps.NonCPS
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import hudson.model.Result as HudsonResult

/**
 * Enumeration for pipeline build results
 * Extends the existing Jenkins Results with still failing, still unstable and fixed to bring back the
 * extmail functionality known from the graphical Jenkins job configuration interface
 */
@SuppressFBWarnings('ME_ENUM_FIELD_SETTER')
enum Result {

  NOT_BUILD(HudsonResult.NOT_BUILT, 3),
  ABORTED(HudsonResult.ABORTED, 4),
  FAILURE(HudsonResult.FAILURE, 2),
  UNSTABLE(HudsonResult.UNSTABLE, 1),
  SUCCESS(HudsonResult.SUCCESS, 0),
  STILL_FAILING(HudsonResult.FAILURE, "STILL FAILING", 2),
  STILL_UNSTABLE(HudsonResult.UNSTABLE, "STILL UNSTABLE", 1),
  FIXED(HudsonResult.SUCCESS, "FIXED", 0)

  HudsonResult hudsonResult

  String name

  Integer ordinal

  Result(HudsonResult r, String name, Integer ordinal) {
    this.hudsonResult = r
    this.name = name
    this.ordinal = ordinal
  }

  Result(HudsonResult r, Integer ordinal) {
    this(r, r.toString(), ordinal)
  }

  @NonCPS
  static Result fromString(String s) {
    if (s == null) return null
    for (r in values()) {
      if (s.equalsIgnoreCase(r.toString())) return r
    }

    return FAILURE
  }

  @NonCPS
  @Override
  String toString() {
    return name
  }

  @NonCPS
  boolean isWorseThan(Result that) {
    return this.ordinal > that.ordinal
  }

  @NonCPS
  boolean isWorseOrEqualTo(Result that) {
    return this.ordinal >= that.ordinal
  }

  @NonCPS
  boolean isBetterThan(Result that) {
    return this.ordinal < that.ordinal
  }

  @NonCPS
  boolean isBetterOrEqualTo(that) {
    return this.ordinal <= that.ordinal
  }
}
