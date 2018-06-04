# credentials

This part of the pipeline library provides easy to use functions for credential auto lookup.

# Table of contents

* [`lookupScmCredential`](#lookupscmcredentialstring-scmurl)
* [`lookupSshCredential`](#lookupsshcredentialstring-host)
* [Related classes](#related-classes)

## `lookupScmCredential(String scmUrl)`

Performs an auto lookup for SCM credentials for the given `scmUrl`.

## `lookupSshCredential(String host)`

Performs an auto lookup for SSH credentials for the given `host`.


## Related classes
* [`Credential.groovy`](../src/io/wcm/devops/jenkins/pipeline/credentials/Credential.groovy)
* [CredentialParser.groovy](../src/io/wcm/devops/jenkins/pipeline/credentials/CredentialParser.groovy)
* [PatternMatcher.groovy](../src/io/wcm/devops/jenkins/pipeline/utils/PatternMatcher.groovy)
