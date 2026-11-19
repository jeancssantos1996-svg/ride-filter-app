#!/bin/sh
# Gradle Wrapper script
# This is a simplified version of the official wrapper to ensure it works in GH Actions
set -e
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
  exit 1
fi
echo "Using Gradle Wrapper for Android Build..."
# Attempt to use the system gradle to initialize the wrapper properly if available, 
# but for GH Actions we can just use the pre-installed gradle and tell it to act as wrapper
gradle assembleDebug
