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
import io.wcm.devops.jenkins.pipeline.environment.EnvironmentConstants
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Sets the build name depending on the availability of the GIT_BRANCH environment variable.
 *
 */
void call() {
    Logger log = new Logger(this)
    // set default versions
    String versionNumberString = '#${BUILD_NUMBER}'
    // check if GIT_BRANCH env var is available
    if (env.getProperty(EnvironmentConstants.GIT_BRANCH) != null) {
        versionNumberString = '#${BUILD_NUMBER}_${' + EnvironmentConstants.GIT_BRANCH + '}'
    }
    // create the versionNumber string
    def version = VersionNumber(projectStartDate: '1970-01-01', versionNumberString: versionNumberString, versionPrefix: '')
    log.info("created versionNumber number", version)
    // set the builds display name
    currentBuild.setDisplayName(version)
}
