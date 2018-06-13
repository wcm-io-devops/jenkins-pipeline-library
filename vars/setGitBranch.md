# setGitBranch

With jenkins pipeline the `GIT_BRANCH` environment variable disappeared.
One the one hand this is obvious since you can work with several scms in
one pipeline, on the other hand this functionality is handy when you are
working with one scm.

Calling this step will bring back this functionality by setting the
`GIT_BRANCH` to the best available value.

:bulb: This step works best when you add the `LocalBranch` extension
during scm checkout:

![checkout-to-local-branch](../docs/assets/checkout-scm/checkout-to-local-branch.png)

or via [`checkoutScm`](checkoutScm.groovy) step. See
[`Example 4`](checkoutScm.md#example-4-checkout-with-userremoteconfigs)

:bulb: If you are using the [`checkoutScm`](checkoutScm.groovy) step from
the library this step will be automatically called for your convenience.

## How does it work?

These steps are executed by the step in order to detect/retrieve the
`GIT_BRANCH`

1. Check if `GIT_BRANCH` is already available
2. Check if `BRANCH_NAME` environment variable is set (this is the case
   in Multibranch pipeline builds)
3. Try to retrieve via executing `git branch` (Therefore the
   `LocalBranch` extension has to be enabled)
4. Fallback: Use the short git commit hash
