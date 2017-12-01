# Credentials

The pipeline library supports the loading of Jenkins credential
references from json files by using the [Pipeline Utility Steps Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin)

These references can be used to auto lookup credential ids based on
patterns. This can be useful to provide automatic ssh keys for test
servers or credentials for scm checkouts.

Based on the rules for writing libraries these json files must be places
below the resources folder.

:bulb: The library only works with references/ids so your credentials
remain safe in the Jenkins instance.

:bulb: See
[Extending with Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)
for more information

# Table of contents
* [JSON Format](#json-format)
* [Using Credentials](#using-credentials)
* [Step examples](#step-examples)
  * [withCredentials](#withcredentials)
  * [GIT checkout](#git-checkout)
  * [SSH Agent](#ssh-agent)
* [Related classes](#related-classes)


## JSON Format

In order to parse the files correctly they must be in the following format:

```json
[
  {
    "pattern": "git@git-ssh\.domain\.tld",
    "id": "ssh-git-credentials-id",
    "comment": "ssh-git-credentials-comment",
    "username": "name-of-the-user"
  },
  {
    "pattern": "https:\/\/git-http\.domain\.tld",
    "id": "https-git-credentials-id",
    "comment": "https-git-credentials-comment"
    "username": "name-of-the-user"
  }
]
```

The properties `pattern` and `id` are mandatory, the `comment` and
`username` properties are optional and can be omitted.

:bulb: When configuring credentials for the
[`transferScp`](../vars/transferScp.md) the `username` should be set!

## Using credentials

In order to use credentials inside your pipeline script you have to
* load
* parse and
* search for a credential based on a pattern

:bulb: The pattern is treated as regular expression

The Example is based on the [`checkoutScm`](../vars/checkoutScm.md) step.
This step loads a json from `resources/credentials/scm/credentials.json`
and matches the incoming scm url against the entries to find the
credential id to use for checkout

```groovy
Credential autoLookupSCMCredentials(String scmUrl) {
    // load the json
    JsonLibraryResource jsonRes = new JsonLibraryResource((DSL) this.steps, CredentialConstants.SCM_CREDENTIALS_PATH)
    JSON credentialJson = jsonRes.load()
    // parse the credentials
    CredentialParser parser = new CredentialParser()
    List<Credential> credentials = parser.parse(credentialJson)
    // try to find matching credential and return the credential
    PatternMatcher matcher = new PatternMatcher()
    return (Credential) matcher.getBestMatch(scmUrl, credentials)
}
```

:bulb: Refer to [PatternMatching](pattern-matching.md) for more
information on how the `getBestMatch` algorithm works

### Step examples

If you have retrieved a
[Credential](../src/io/wcm/devops/jenkins/pipeline/credentials/Credential.groovy)
object stored in the variable `foundCredential` you can use this for example in the
following ways:

#### withCredentials
```groovy
withCredentials([usernamePassword(credentialsId: foundCredential.id, passwordVariable: 'passwordVar', usernameVariable: 'usernameVar')]) {
    // some block
}
```

#### GIT checkout
```groovy
checkout(
    [$class: 'GitSCM', 
    branches: [[name: '*/master']], 
    doGenerateSubmoduleConfigurations: false, 
    extensions: [], 
    submoduleCfg: [], 
    userRemoteConfigs: [[credentialsId: foundCredential.id, url: 'git@domain.tld:group/project.git']]])

```

#### SSH Agent
```groovy
sshagent([foundCredential.id]) {
    ssh "${foundCredential.host}@localhost" 'pwd'
}
```

## Related classes
* [Credential](../src/io/wcm/devops/jenkins/pipeline/credentials/Credential.groovy)
* [CredentialConstants](../src/io/wcm/devops/jenkins/pipeline/credentials/CredentialConstants.groovy)
* [CredentialParser](../src/io/wcm/devops/jenkins/pipeline/credentials/CredentialParser.groovy)
* [PatternMatchable](../src/io/wcm/devops/jenkins/pipeline/model/PatternMatchable.groovy)
* [PatternMatcher](../src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)
