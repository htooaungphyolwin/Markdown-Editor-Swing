#!/bin/bash
# Run the Markdown Editor (thin JAR + cached classpath)
BASEDIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$BASEDIR/build/libs/markdown-editor.jar"
CLASSPATH=$(find "$HOME/.gradle/caches/modules-2/files-2.1" -name "*.jar" 2>/dev/null | tr '\n' ':')
exec java -cp "$JAR:$CLASSPATH" com.mdeditor.Main
