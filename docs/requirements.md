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

Please refer to the
[wcm_io_devops.jenkins_pipeline_library Ansible role defaults](https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library/blob/master/defaults/main.yaml)
for a up to date list for the required plugins and their supported
versions.

:exclamation: The library may run with newer versions, but this is not
tested.

:bulb: For older Jenkins versions and their tested plugins you can have
a look here:
https://github.com/wcm-io-devops/ansible-jenkins-pipeline-library/releases.
