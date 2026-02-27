#!/bin/bash
# Build script for Vinkällaren with correct Java

export JAVA_HOME="/usr/local/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Java version:"
"$JAVA_HOME/bin/java" -version 2>&1 | head -2

echo ""
echo "Starting build..."
cd ~/vinkallaren

GRADLE_BIN="$HOME/.gradle/wrapper/dists/gradle-8.13-bin/5xuhj0ry160q40clulazy9h7d/gradle-8.13/bin/gradle"

"$GRADLE_BIN" clean assembleDebug --console=plain 2>&1

echo ""
echo "Build complete. Checking for APK..."
ls -la app/build/outputs/apk/debug/*.apk 2>/dev/null || echo "No APK found"
