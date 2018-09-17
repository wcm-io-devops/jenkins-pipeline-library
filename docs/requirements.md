# Requirements

* [Ansible utility role](#ansible-utility-role)
* [script security / script approval](#script-security--script-approval)
* [Jenkins + Plugins](#jenkins--plugins)

## Ansible utility role

It is recommended to use the Ansible role
[wcm_io_devops.jenkins_pipeline_library](https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library)
to automatically setup/configure a Jenkins instance to work with the
pipeline library.

This role will also setup the needed script approvals!

## script security / script approval

One of the main goals of the wcm-io-devops Jenkins Pipeline Library was
develop a shared library which works within in the Groovy Sandbox
without any script approval.

Starting with workflow-support 2.18 this is not possible anymore because
for example accessing static fields and methods, even when they come
from a shared pipeline lib, they must be approved (see
https://issues.jenkins-ci.org/browse/JENKINS-49597).

However we try to limit the needed script security approvals to
absolutely minimum.

Please have a look at the
[`scriptApproval.xml`](assets/requirements/scriptApproval.xml) for all
signatures that need approval.

## Jenkins + Plugins

The Pipeline Library is currently tested with the following versions.
:bulb: For older Jenkins versions and their tested plugins you can have
a look here:
https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library/releases.

Jenkins: 2.181.1

| Name                               | Version   |
|:-----------------------------------|:----------|
| ace-editor                         | 1.1       |
| authentication-tokens              | 1.3       |
| apache-httpcomponents-client-4-api | 4.5.5-3.0 |
| branch-api                         | 2.0.20    |
| cloudbees-folder                   | 6.6       |
| config-file-provider               | 3.1       |
| credentials                        | 2.1.18    |
| credentials-binding                | 1.16      |
| display-url-api                    | 2.2.0     |
| docker-workflow                    | 1.17      |
| docker-commons                     | 1.13      |
| durable-task                       | 1.25      |
| git                                | 3.9.1     |
| git-client                         | 2.7.3     |
| git-server                         | 1.7       |
| handlebars                         | 1.1.1     |
| jackson2-api                       | 2.8.11.3  |
| junit                              | 1.25      |
| jquery-detached                    | 1.2.1     |
| jsch                               | 0.1.54.2  |
| managed-scripts                    | 1.4       |
| mailer                             | 1.21      |
| managed-scripts                    | 1.4       |
| matrix-project                     | 1.13      |
| momentjs                           | 1.1.1     |
| nodejs                             | 1.2.6     |
| pipeline-aggregator-view           | 1.8       |
| pipeline-build-step                | 2.7       |
| pipeline-graph-analysis            | 1.7       |
| pipeline-input-step                | 2.8       |
| pipeline-milestone-step            | 1.3.1     |
| pipeline-model-declarative-agent   | 1.1.1     |
| pipeline-model-definition          | 1.3.2     |
| pipeline-model-api                 | 1.3.2     |
| pipeline-model-extensions          | 1.3.2     |
| pipeline-rest-api                  | 2.10      |
| pipeline-stage-step                | 2.3       |
| pipeline-stage-tags-metadata       | 1.3.2     |
| pipeline-stage-view                | 2.10      |
| pipeline-utility-steps             | 2.1.0     |
| plain-credentials                  | 1.4       |
| rebuild                            | 1.28      |
| scm-api                            | 2.2.7     |
| script-security                    | 1.46      |
| ssh-credentials                    | 1.14      |
| structs                            | 1.14      |
| token-macro                        | 2.5       |
| workflow-api                       | 2.29      |
| workflow-basic-steps               | 2.11      |
| workflow-cps                       | 2.55      |
| workflow-cps-global-lib            | 2.11      |
| workflow-durable-task-step         | 2.21      |
| workflow-job                       | 2.25      |
| workflow-multibranch               | 2.20      |
| workflow-scm-step                  | 2.6       |
| workflow-step-api                  | 2.16      |
| workflow-support                   | 2.20      |
