# setScmUrl

With jenkins pipeline the `SCM_URL` environment variable disappeared.
One the one hand this is obvious since you can work with several scms in
one pipeline, on the other hand this functionality is handy when you are
working with one scm.

Calling this step will bring back this functionality by setting the
`SCM_URL` to the best available value.

## Variant 1 (using SCM config)
Assuming you are using the [`checkoutScm`](checkoutScm.groovy) step in
your project the `SCM_URL` will be automatically set by this step.

In this case the step tries to retrieve the `SCM_URL` from
the config object:

```groovy
import static io.wcm.tooling.jenkins.pipeline.utils.ConfigConstants.*

setScmUrl(
    (SCM): [
        (SCM_URL): "git@domain.tld/group/project.git"
    ]
)
```

## Variant 2 (GIT command line)
This variant is the fallback. By calling `git config remote.origin.url`
the remote URL is retrieved and set to the environment variable