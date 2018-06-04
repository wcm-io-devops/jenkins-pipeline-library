#!/bin/bash
#-
# #%L
# wcm.io
# %%
# Copyright (C) 2017 - 2018 wcm.io DevOps
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
#

# This managed shell script purges SNAPSHOT artifacts from a maven repository

# define variables / defaults
MAVEN_REPO="$HOME/.m2/repository"
LOG_LVL=4
DRY_RUN=false
REGEX="\/?\s*(.+)"
TMP_FILES_TO_DELETE="/tmp/$USER-files_to_delete.txt"
LOG_SCOPE="managedScripts:shell:maven:mvn-purge-snapshots"

# read arguments
while [ $# -gt 0 ]; do
  case "$1" in
    --repo=*)
        MAVEN_REPO="${1#*=}"
        ;;
    --loglvl=*)
        LOG_LVL="${1#*=}"
        ;;
    --dryrun)
        DRY_RUN=true
        ;;
    --help)
        echo "use --repo=/path/to/repo to specify a repo"
        echo "use --loglevel=[0-n] to specify a loglevel"
        echo "use --dryrun to execute a run without really deleting anything."
        exit 0
        ;;
  esac
  shift
done

echo "LogLevel: $LOG_LVL"

#
# Logging utilities
#

LOG_LVL_ALL=0
LOG_LVL_TRACE=2
LOG_LVL_DEBUG=3
LOG_LVL_INFO=4
LOG_LVL_DEPRECATED=5
LOG_LVL_WARN=6
LOG_LVL_ERROR=7
LOG_LVL_FATAL=8
LOG_LVL_NONE=999999

log() {
  if $DRY_RUN
  then
    echo -e "[$LOG_SCOPE] (DRYRUN) [$1] $2"
  else
    echo -e "[$LOG_SCOPE] [$1] $2"
  fi
}

trace() { if [ $LOG_LVL_TRACE -ge $LOG_LVL ]; then log "TRACE" "$1"; fi }
debug() { if [ $LOG_LVL_DEBUG -ge $LOG_LVL ]; then log "DEBUG" "$1"; fi }
info() { if [ $LOG_LVL_INFO -ge $LOG_LVL ]; then log "INFO" "$1"; fi }
deprecated() { if [ $LOG_LVL_DEPRECATED -ge $LOG_LVL ]; then log "DEPRECATED" "$1"; fi }
warn() { if [ $LOG_LVL_WARN -ge $LOG_LVL ]; then log "WARN" "$1"; fi }
error() { if [ $LOG_LVL_ERROR -ge $LOG_LVL ]; then log "ERROR" "$1"; fi }
fatal() { if [ $LOG_LVL_FATAL -ge $LOG_LVL ]; then log "FATAL" "$1"; fi }

#
# Logic
#

# clear files to delete
rm -f "$TMP_FILES_TO_DELETE"

info "Using maven repo : '$MAVEN_REPO'";
info "Using temp file  : '$TMP_FILES_TO_DELETE'";
info "Dry run          : '$DRY_RUN'";
info "Search for snapshots in repository";

find "${MAVEN_REPO}" -name '*-SNAPSHOT*' -exec dirname {} \; >> ${TMP_FILES_TO_DELETE}
DELETE_FILES_COUNT=($(wc -l "${TMP_FILES_TO_DELETE}"))
info "Deleting ${DELETE_FILES_COUNT} snapshot file(s)/dir(s) from repository";

for x in `cat ${TMP_FILES_TO_DELETE}`;
do
    debug "Deleting $x"
    if  [ $DRY_RUN = false ]
    then
        rm -rf "$x";
    fi
done
