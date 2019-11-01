#!/usr/bin/env bash
set -xeuf -o pipefail

# command override. e.g. docker run -it <image> /bin/bash
cmd=${1:-}
if [[ -z "$cmd" ]]; then
  exec "$@"
fi

# no command override. launch web app in the same process
JAVA_HEAP_SIZE=${JAVA_HEAP_SIZE:=256M}
JAVA_OPTS=${JAVA_OPTS:=-Xms${JAVA_HEAP_SIZE} -Xmx${JAVA_HEAP_SIZE}}
exec java ${JAVA_OPTS} -jar app.jar 2>&1
