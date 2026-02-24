#!/usr/bin/env bash
# Run common unit tests with Java 21 (SDKMAN)
set -e
export JAVA_HOME="${HOME}/.sdkman/candidates/java/21.0.10-amzn"
if [ ! -d "$JAVA_HOME" ]; then
  echo "Java 21.0.10-amzn not found. Run: sdk use java 21.0.10-amzn"
  exit 1
fi
echo "Using JAVA_HOME=$JAVA_HOME"
"$JAVA_HOME/bin/java" -version
./gradlew :common:test --no-daemon "$@"
