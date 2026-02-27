#!/bin/bash
# Fix Java/Kotlin for Android Studio

# Add Java to PATH
export JAVA_HOME="/usr/local/opt/openjdk"
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version

echo "Java fixed! Now run your build:"
echo "./gradlew assembleDebug"
