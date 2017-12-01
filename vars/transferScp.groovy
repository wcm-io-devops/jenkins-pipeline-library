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
import io.wcm.devops.jenkins.pipeline.credentials.CredentialConstants
import io.wcm.devops.jenkins.pipeline.credentials.CredentialParser
import io.wcm.devops.jenkins.pipeline.shell.ScpCommandBuilderImpl
    import io.wcm.devops.jenkins.pipeline.ssh.SSHTarget
    import io.wcm.devops.jenkins.pipeline.utils.PatternMatcher
import io.wcm.devops.jenkins.pipeline.utils.logging.Logger
import io.wcm.devops.jenkins.pipeline.utils.resources.JsonLibraryResource
import net.sf.json.JSON
import org.jenkinsci.plugins.workflow.cps.DSL

import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.SCP

/**
 * Utility step to transfer files via scp.
 * This step uses the sshAgentWrapper for ssh credential auto lookup
 *
 * @param config Configuration options for the step
 */
void call(Map config = null) {
    config = config ?: [:]
    Logger log = new Logger(this)

    // retrieve the configuration and set defaults
    Map scpConfig = (Map) config[SCP] ?: [:]

    log.trace("SCP config: ", scpConfig)

    // initialize the command builder
    ScpCommandBuilderImpl commandBuilder = new ScpCommandBuilderImpl((DSL) this.steps)
    commandBuilder.applyConfig(scpConfig)

    SSHTarget sshTarget = new SSHTarget(commandBuilder.getHost())

    // use the sshAgentWrapper for ssh credential auto lookup
    sshAgentWrapper([sshTarget]) {
        // provide credentials from sshAgentWrapper to commandbuilder
        commandBuilder.setCredential(sshTarget.getCredential())
        command = commandBuilder.build()
        log.info("The following scp command will be executed", command)
        // execute the command
        sh(command)
    }
}
