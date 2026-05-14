#!/bin/bash
# Build script for Markdown Editor (javac direct — no Gradle required)

set -e

BASEDIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$BASEDIR/build"
CLASSES_DIR="$BUILD_DIR/classes"
LIBS_DIR="$BUILD_DIR/libs"
SRC_DIR="$BASEDIR/src/main/java"

# Find flexmark jars from Gradle cache
CACHE_DIR="$HOME/.gradle/caches/modules-2/files-2.1"
CLASSPATH=""

if [ -d "$CACHE_DIR" ]; then
    for jar in $(find "$CACHE_DIR" -name "*.jar" 2>/dev/null); do
        CLASSPATH="$CLASSPATH:$jar"
    done
fi

echo ">>> Compiling..."
mkdir -p "$CLASSES_DIR"
find "$SRC_DIR" -name "*.java" > /tmp/sources-$$.txt
javac -d "$CLASSES_DIR" -cp "$CLASSPATH" @/tmp/sources-$$.txt
rm -f /tmp/sources-$$.txt

echo ">>> Packaging JAR..."
mkdir -p "$LIBS_DIR"
echo "Main-Class: com.mdeditor.Main" > /tmp/MANIFEST-$$.MF
cd "$CLASSES_DIR" && jar cfm "$LIBS_DIR/markdown-editor.jar" /tmp/MANIFEST-$$.MF .
rm -f /tmp/MANIFEST-$$.MF

echo ">>> Done: $LIBS_DIR/markdown-editor.jar ($(du -h "$LIBS_DIR/markdown-editor.jar" | cut -f1))"
