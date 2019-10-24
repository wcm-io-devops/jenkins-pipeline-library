#!/bin/bash
# source: https://gist.github.com/joechrysler/6073741
git show-branch -a 2>/dev/null | grep '\*' | grep -v `git rev-parse --abbrev-ref HEAD` | head -n1 | sed 's/.*\[\(.*\)\].*/\1/' | sed 's/[\^~].*//'