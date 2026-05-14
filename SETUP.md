# Setup & Build Guide

## Prerequisites

- **JDK 17+** (Java Development Kit)
- **Git** (optional, for version control)

> Gradle and flexmark dependencies are handled automatically — no manual download needed.

---

## Quick Start

### 1. Clone or navigate to the project

```bash
cd /path/to/markdown-editor
```

### 2. Build the project

**Option A — Gradle (recommended):**

```bash
./gradlew build
./gradlew shadowJar    # creates fat JAR with all deps
```

**Option B — Direct javac (no Gradle needed):**

```bash
./build.sh
```

This compiles all source files and packages `build/libs/markdown-editor.jar`.

### 3. Run the application

**Option A — via fat JAR (Gradle build):**

```bash
java -jar build/libs/markdown-editor-1.0.0-all.jar
```

**Option B — via thin JAR (javac build):**

```bash
./run.sh
```

**Option C — via Gradle:**

```bash
./gradlew run
```

---

## Project Structure

```
src/main/java/com/mdeditor/
├── Main.java                   # Entry point
├── ui/
│   ├── MainFrame.java          # Main window (JFrame + JSplitPane)
│   ├── EditorPanel.java        # JTextArea editor (monospaced)
│   ├── PreviewPanel.java       # JEditorPane HTML preview
│   ├── StatusBar.java          # Line/col, word count, modified indicator
│   └── reading/
│       ├── ReadingManager.java # Orchestrates Pop-out & Focus modes
│       ├── ReadingFrame.java   # Separate JFrame for Pop-out Reader
│       └── ReadingToolBar.java # Reader window toolbar
├── markdown/
│   └── MarkdownProcessor.java  # flexmark wrapper (md → html)
├── document/
│   ├── FileManager.java        # Open/save file logic
│   └── DocumentState.java      # Undo, dirty flag, file path
├── formatting/
│   └── InsertActions.java      # Insert markdown syntax at cursor
├── preview/
│   └── ThemeManager.java       # CSS theming (Light/Dark/Sepia)
└── util/
    └── KeyboardShortcuts.java  # Key bindings (Ctrl+S, etc.)

src/main/resources/
└── themes/
    ├── light.css
    ├── dark.css
    └── sepia.css
```

---

## Common Tasks

### Build without running tests

```bash
./gradlew build -x test
```

### Clean build artifacts

```bash
./gradlew clean
./gradlew build
```

### Run with a specific JDK

```bash
JAVA_HOME=/path/to/jdk-17 ./gradlew run
```

### View available Gradle tasks

```bash
./gradlew tasks
```

---

## Build Output

| Command | Output |
|---|---|
| `./gradlew compileJava` | `build/classes/java/main/` |
| `./gradlew shadowJar` | `build/libs/markdown-editor-1.0.0-all.jar` (~22MB) |
| `./gradlew clean` | Deletes `build/` directory |

---

## Troubleshooting

| Problem | Fix |
|---|---|
| `Java not found` | Install JDK 17+ and set `JAVA_HOME` |
| `gradlew: Permission denied` | Run `chmod +x gradlew` |
| Gradle download slow | The wrapper downloads Gradle 8.5 automatically on first run — this is a one-time step |
| flexmark import errors in IDE | The IDE needs to import the Gradle project (open `build.gradle` as project) |
