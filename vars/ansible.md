# Ansible

The ansible part of the library implements
* Ansible Playbook execution by providing a configuration object
* Checking out Ansible Galaxy requirements to track role changes
* Getting Ansible Galaxy role info from the galaxy API

## Table of contents

* [Common configuration options](#common-configuration-options)
* [`checkoutRoles(Map config)`](#checkoutrolesmap-config)
* [`checkoutRoles(String galaxyRoleFile)`](#checkoutrolesstring-galaxyrolefile)
  * [Example of a `roles.yml`](#example-of-a-rolesyml)
  * [Process](#process)
* [`execPlaybook(Map config)`](#execplaybookmap-config)
  * [Features](#features)
    * [`--extra-vars` as JSON](#--extra-vars-as-json)
    * [Inject Build parameters into `--extra-vars`](#inject-build-parameters-into---extra-vars)
    * [Extra parameters](#extra-parameters)
    * [Configuration Options](#execplaybookmap-config)
* [`getGalaxyRoleInfo(Role role)`](#getgalaxyroleinforole-role)
    * [Example](#example)
* [`installRoles(Map config`)](#installrolesmap-config)
  * [Configuration Options](#installrolesmap-config)

## Common configuration options

These configuration options can be used for each build step that
consumes the config map.

All configuration options must be inside the `ansible`
([`ConfigConstants.ANSIBLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

### `installation`

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_INSTALLATION`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

## `checkoutRoles(Map config)`

This is an adapter step for the
[`checkoutRoles(String galaxyRoleFile)`](#checkoutrolesstring-galaxyrolefile)
step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

ansible.checkoutRoles(
        (ANSIBLE): [
            (ANSIBLE_GALAXY_ROLE_FILE)  : '<roles-path>' 
        ]
    )
```

## `checkoutRoles(String galaxyRoleFile)`

This step checks out all ansible galaxy role file into subdirectories of
the workspace (below `.roleRequirements`) to track SCM changes in the
depending roles. For Ansible Galaxy roles the `src` Attribute is used,
for `scm` roles the `name` attribute is used

This currently works for:

* Ansible Galaxy roles (with and without version)
* Git scm Roles

:bulb: The roles are checkout into sub folders using the `name` (`src` for Ansible Galaxy Roles) of the role.

### Example of a `roles.yml`
```yaml
- src: williamyeh.oracle-java
- src: tecris.maven
  version: v3.5.2
- src: https://github.com/wcm-io-devops/ansible-aem-cms.git
  name: aem-cms
  scm: git
- src: https://github.com/wcm-io-devops/ansible-aem-service.git
  name: aem-service
  scm: git
  version: develop
```

This `roles.yml` will result in a checkout of four repositories:
* https://github.com/William-Yeh/ansible-oracle-java.git (master) into folder "williamyeh.oracle-java"
* https://github.com/tecris/ansible-maven.git (tag v3.5.2)  into folder "tecris.maven"
* https://github.com/wcm-io-devops/ansible-aem-cms.git (master) into folder "aem-cms"
* https://github.com/wcm-io-devops/ansible-aem-service.git (master) into folder "aem-service"

### Process

1. Load the provided yaml file
2. Parse the roles into [`Role`](../src/io/wcm/devops/jenkins/pipeline/tools/ansible/Role.groovy) objects by using [`RoleRequirements`](../src/io/wcm/devops/jenkins/pipeline/tools/ansible/RoleRequirements.groovy)
3. Get API info for each Ansible Galaxy Role by using [`getGalaxyRoleInfo`](#getgalaxyroleinforole-role)
   1. When API info is available set the github url and branch into the Role
4. Transform the roles into configurations for the [`checkoutScm`](checkoutScm.groovy) step
5. Checkout the SCM using the [`checkoutScm`](checkoutScm.groovy) step

## `execPlaybook(Map config)`

This step is used to execute a Ansible Playbook.

### Features
#### `--extra-vars` as JSON

The step transforms all given extra vars into JSON before calling the Ansible Playbook.
This ensures that the types like `boolean` and `integer` are retained.

:bulb: These extra vars are combined with build parameters when `ANSIBLE_INJECT_PARAMS` is enabled.

**Example**

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
    (ANSIBLE) : [
        (ANSIBLE_INSTALLATION) : '<ansible-installation-id>',
        (ANSIBLE_PLAYBOOK) : 'path/to/playbook.yml',
        (ANSIBLE_INVENTORY) : 'path/to/inventory',
        (ANSIBLE_EXTRA_VARS) : [
            "string": "value",
            "boolean" : true,
            "integer" : 1,
            "list" : [1,2,3,4]
        ]
    ]
]

ansible.execPlaybook(config)
```

This config will execute the ansible playbook with the following `--extra-vars` parameter
```
ansible-playbook --extra-vars '{"string":"value","boolean":true,"integer":1,"list":[1,2,3,4]}' [...]
```

#### Inject Build parameters into `--extra-vars`

When enabled the step will automatically add all build parameters as extra variables an pass it to the playbook.
:bulb: These extra vars are combined with the variables defined via `ANSIBLE_EXTRA_VARS`.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

properties([
	parameters([
		booleanParam(defaultValue: false, description: '...', name: 'boolparam'), 
		choice(choices: 'choice1\nchoice2', description: '..', name: 'choiceparam'), 
		string(defaultValue: 'stringvalue', description: '..', name: 'stringparam')]), 
	]
)

Map config = [
    (ANSIBLE) : [
        (ANSIBLE_INSTALLATION) : '<ansible-installation-id>',
        (ANSIBLE_PLAYBOOK) : 'path/to/playbook.yml',
        (ANSIBLE_INVENTORY) : 'path/to/inventory',
        (ANSIBLE_INJECT_PARAMS) : true
    ]
]

ansible.execPlaybook(config)
```

This config will execute the ansible playbook with the following `--extra-vars` parameter
```
ansible-playbook --extra-vars '{"boolparam":false,"choiceparam":"choice1","stringparam":"defaultValue"}' [...]
```

:exclamation: Pleaes note that all parameters are injected. If you want to decide which parameters are added as extra params use the `ANSIBLE_EXTRA_VARS` configuration option.

#### Extra parameters

The step provides a convenient way to add extra parameters to the command line.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
    (ANSIBLE) : [
        (ANSIBLE_INSTALLATION) : '<ansible-installation-id>',
        (ANSIBLE_PLAYBOOK) : 'path/to/playbook.yml',
        (ANSIBLE_INVENTORY) : 'path/to/inventory',
        (ANSIBLE_EXTRA_PARAMETERS) : ["-v"]
    ]
]

ansible.execPlaybook(config)
```

This config will execute the ansible playbook with the `-v` parameter
```
ansible-playbook -v [...]
```

### Configuration Options

<a id="execplaybook-configuration-options" />

Complete list of all configuration options.

All configuration options must be inside the `ansible`
([`ConfigConstants.ANSIBLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

ansible.execPlaybook(
        (ANSIBLE): [
            (ANSIBLE_COLORIZED)              : true,
            (ANSIBLE_EXTRA_PARAMETERS)       : ["-list","-of","-params"],
            (ANSIBLE_EXTRA_VARS)             : [ "<name1>" : "<value1>", "<name2>" : "<value2>" ],
            (ANSIBLE_FORKS)                  : 5,
            (ANSIBLE_INSTALLATION)           : '<ansible-installation-id>',
            (ANSIBLE_INVENTORY)              : "<path/to/inventory>",
            (ANSIBLE_LIMIT)                  : "<limit>",
            (ANSIBLE_PLAYBOOK)               : "<path/to/playbook>",
            (ANSIBLE_CREDENTIALS_ID)         : "<credentials-id>",
            (ANSIBLE_SKIPPED_TAGS)           : "<tags-to-skip>",
            (ANSIBLE_START_AT_TASK)          : "<task-to-start-at>",
            (ANSIBLE_SUDO)                   : false,
            (ANSIBLE_SUDO_USER)              : "<ansible-sudo-user>",
            (ANSIBLE_TAGS)                   : "<tags-to-execute>",
            (ANSIBLE_INJECT_PARAMS)          : false,
            (ANSIBLE_VAULT_CREDENTIALS_ID)   : "<vault-credentials-id>",
        ]
    )
```

#### `colorized` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_COLORIZED`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`true`|

Controls the colorized output of ansible. Default is set to true.

#### `credentialsId` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_CREDENTIALS_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Use this option to pass SSH credentials.

:bulb: It is recommended to use the [`sshAgentWrapper`](sshAgentWrapper.md) instead.

#### `extraParameters` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_EXTRA_PARAMETERS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`List` of `String`|
|Default|`[]`|

Extra parameters that will be passed to ansible-playbook commandline

**Example:**
This example will add `-v` to the command line.
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
    (ANSIBLE) : [
        (ANSIBLE_EXTRA_PARAMETERS) : ["-v"]
        // ...
    ]
]
```

#### `extraVars` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_EXTRA_VARS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Map`|
|Default|`[:]`|

Can be used to define `--extra-vars` which will be passed in JSON format to the command line.

:bulb: When [`injectParams`](#injectparams-optional) is used they will be combined with the injected build parameters.

**Example:**
This example will add `--extra-vars '{"string":"value","boolean":true,"integer":1,"list":[1,2,3,4]}'` to the command line.
```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

Map config = [
    (ANSIBLE) : [
        (ANSIBLE_INSTALLATION) : '<ansible-installation-id>',
        (ANSIBLE_PLAYBOOK) : 'path/to/playbook.yml',
        (ANSIBLE_INVENTORY) : 'path/to/inventory',
        (ANSIBLE_EXTRA_VARS) : [
            "string": "value",
            "boolean" : true,
            "integer" : 1,
            "list" : [1,2,3,4]
        ]
    ]
]
```

#### `forks` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_FORKS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Integer`|
|Default|`5`|

Controls how many forks will be used during Ansible Playbook execution.

#### `injectParams` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_INJECT_PARAMS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When enabled **all** build parameters are injected into `--extra-vars`*
:bulb: When [`extraVars`](#extravars-optional) are defined they will be combined with the injected params.

#### `installation`

see [Common configuration options](#common-configuration-options)

#### `inventory`

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_INVENTORY`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies the path to the Ansible Playbook inventory.

#### `limit` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_LIMIT`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When set the configured value will be passed as `--limit <value>` to the Ansible Playbook.

#### `playbook`

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_PLAYBOOK`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies the path to the Ansible Playbook.

#### `skippedTags` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_SKIPPED_TAGS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When set the configured value will be passed as `--skip-tags <value>` to the Ansible Playbook.

#### `startAtTask` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_START_AT_TASK`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When set the configured value will be passed as `--start-at-task <value>` to the Ansible Playbook.

#### `tags` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_TAGS`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

When set the configured value will be passed as `--tags <value>` to the Ansible Playbook.

#### `sudo` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_SUDO`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`Boolean`|
|Default|`false`|

When enabled sudo (become) will be used. Combined with [`sudoUser`](#sudouser-optional) setting.

#### `sudoUser` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_SUDO_USER`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies the sudo user to use (become_user)

#### `vaultCredentialsId` (optional)

|||
|---|---|
|Constant|[`ConfigConstants.ANSIBLE_VAULT_CREDENTIALS_ID`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy)|
|Type|`String`|
|Default|`null`|

Specifies the credentials to use for the ansible-vault.

## `getGalaxyRoleInfo(Role role)`

Utility function to the the Ansible Galaxy role info from the API.
:bulb: Works only for Ansible Galaxy Roles

:bulb: This method will return `null` when no info was found.

### Example

This example will return the API role info for the role "tecris.maven"

```groovy
import io.wcm.devops.jenkins.pipeline.tools.ansible.Role
Role role = new Role("tecris.maven")

Object apiInfo = ansible.getGalaxyRoleInfo(role)
```

## `installRoles(Map config)`

This step is used to install Ansible roles specified by a `yml`.

<a id="installroles-configuration-options" />

### Configuration Options

Complete list of all configuration options.

All configuration options must be inside the `ansible`
([`ConfigConstants.ANSIBLE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy))
map element to be evaluated and used by the step.

```groovy
import static io.wcm.devops.jenkins.pipeline.utils.ConfigConstants.*

ansible.installRoles(
        (ANSIBLE): [
            (ANSIBLE_INSTALLATION)       : '<ansible-installation-id>',
            (ANSIBLE_GALAXY_FORCE) : false,
            (ANSIBLE_GALAXY_ROLE_FILE)  : '<roles-path>' 
        ]
    )
```

#### `installation` (optional)

See [Common configuration options](#common-configuration-options)

#### `requirementsForce` (optional)

|                                                                                                                              ||
|:---------|:-------------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.ANSIBLE_GALAXY_FORCE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `Boolean`                                                                                                          |
| Default  | `false`                                                                                                             |

#### `requirementsPath`

|                                                                                                                             ||
|:---------|:------------------------------------------------------------------------------------------------------------------|
| Constant | [`ConfigConstants.ANSIBLE_GALAXY_ROLE_FILE`](../src/io/wcm/devops/jenkins/pipeline/utils/ConfigConstants.groovy) |
| Type     | `String`                                                                                                          |
| Default  | `null`                                                                                                            |

[checkoutRoles(String galaxyRoleFile)]: #checkoutrolesstring-galaxyrolefile

