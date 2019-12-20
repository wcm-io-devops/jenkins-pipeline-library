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

import io.wcm.devops.jenkins.pipeline.credentials.Credential
import io.wcm.devops.jenkins.pipeline.ssh.SSHTarget
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger

/**
 * Adapter step for one ssh target without credential aware parameter
 *
 * @param sshTarget the target to connect to
 * @param body the closure to execute inside the wrapper
 */
void call(String sshTarget, Closure body) {
    this.call([new SSHTarget(sshTarget)], body)
}

/**
 * Adapter step for one ssh target without credential aware parameter
 *
 * @param sshTarget the target to connect to as value object
 * @param body the closure to execute inside the wrapper
 */
void call(SSHTarget sshTarget, Closure body) {
    this.call([sshTarget], body)
}

/**
 * Step for encapsulating the provided body into a sshagent step with ssh credential autolookup
 *
 * @param sshTargets the targets to connect to
 * @param credentialAware The credential aware object where the step should set the found credentials for the first target
 * @param body the closure to execute inside the wrapper
 */
void call(List<SSHTarget> sshTargets, Closure body) {
    Logger log = new Logger(this)

    Map foundCredentials = [:]
    for (int i = 0; i < sshTargets.size(); i++) {
        SSHTarget sshTarget = sshTargets[i]

        // auto lookup ssh credentials
        log.trace("auto lookup credentials for : '${sshTarget.getHost()}'")
        Credential sshCredential = credentials.lookupSshCredential(sshTarget.getHost())
        if (sshCredential != null) {
            log.debug("auto lookup found the following credential for '${sshTarget.getHost()}' : '${sshCredential.id}'")
            foundCredentials[sshCredential.id] = sshCredential
            sshTarget.setCredential(sshCredential)
        } else {
            log.warn("No ssh credential was found for '${sshTarget.getHost()}' during auto lookup. Make sure to configure the credentials! See sshAgentWrapper.md for details.")
        }
    }

    // only use unique credentials
    List sshCredentials = []
    foundCredentials.each {
        String k, Credential v ->
            sshCredentials.push(v.getId())
    }


    log.trace("start ssh agent")
    sshagent(sshCredentials) {
        body()
    }
}
