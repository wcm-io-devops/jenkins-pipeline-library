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
package vars.wrappers.jobs

import io.wcm.devops.jenkins.pipeline.utils.logging.LogLevel
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

/**
 * Runs the wrap.color step
 */
def execute() {
  Logger.init(this, LogLevel.DEBUG)
  Logger log = new Logger(this)

  Map config = [
      (ANSI_COLOR): ANSI_COLOR_VGA
  ]

  log.info("non colorized output - 1")

  wrappers.color(config) {
    log.info("first wrap env.TERM: ${env.TERM}")
    wrappers.color(config) {
      log.info("second wrap env.TERM: ${env.TERM}")
    }
  }

  log.info("non colorized output - 2")
}

return this
