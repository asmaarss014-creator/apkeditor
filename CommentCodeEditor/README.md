# Comment Code Editor

A native Android app that reads the first comment in your pasted code, extracts the filename from it, and automatically saves the entire code to that file in a directory you choose.

## Features

- **Native Android app** built with Kotlin and Jetpack Compose
- **Modern Material 3 UI** with dynamic colors on Android 12+
- **Storage Access Framework (SAF)** for secure, permission-friendly directory selection
- **Multi-language comment support** — works with Python, JavaScript, Java, C/C++, HTML, SQL, Lisp, Batch, PowerShell, and more
- **GitHub Actions CI/CD** — push to GitHub and the APK is built automatically

## How It Works

1. **Select Directory** — Choose where files will be saved using the system file picker
2. **Paste Code** — Paste your code into the editor
3. **First Comment = Filename** — The first comment in your code must contain the filename wrapped in the language's comment syntax:
   ```python
   # main.py #
   print("Hello, World!")
   ```
   ```javascript
   // app.js //
   console.log("Hello, World!");
   ```
   ```c
   /* main.c */
   #include <stdio.h>
   int main() { printf("Hello!"); return 0; }
   ```
   ```html
   <!-- index.html -->
   <!DOCTYPE html>
   <html>...</html>
   ```
4. **Execute & Save** — Tap the button. The app extracts the filename and saves the full code.

## Supported Comment Formats

| Language | Example |
|----------|---------|
| Python, Shell, Ruby, YAML | `# main.py #` |
| JavaScript, Java, Kotlin, Go, Rust | `// app.js //` |
| C, C++, CSS | `/* main.c */` |
| HTML, XML | `<!-- index.html -->` |
| SQL, Lua, Haskell | `-- schema.sql --` |
| Lisp, Clojure, Scheme | `;; config.lisp ;;` |
| Windows Batch | `REM batch.bat REM` |
| PowerShell | `<# script.ps1 #>` |

> **Note:** The app skips shebang lines (`#!/usr/bin/env python3`) automatically and looks for the first real comment.

## Building with GitHub Actions

This project includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that builds the debug APK automatically.

### Setup

1. Create a new repository on GitHub
2. Push this project to the repository:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
   git push -u origin main
   ```
3. Go to **Actions** tab in your GitHub repository
4. The workflow will run automatically on push, or you can trigger it manually with **Run workflow**
5. Once complete, download the APK from the workflow artifacts

### Local Build (Optional)

If you prefer to build locally:

1. Install Android Studio Hedgehog (2023.1.1) or later
2. Open the project in Android Studio
3. Sync Gradle and build: **Build > Build Bundle(s) / APK(s) > Build APK(s)**

## Installation

1. Download the APK from GitHub Actions artifacts
2. Transfer it to your Samsung S21 Ultra (or any Android 7.0+ device)
3. Tap the APK to install (you may need to allow "Install unknown apps" for your file manager)
4. Open **Comment Editor** and start saving code!

## Requirements

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34
- **Java:** 17
- **Kotlin:** 1.9.20

## Project Structure

```
CommentCodeEditor/
├── .github/workflows/build-apk.yml   # CI/CD workflow
├── app/
│   ├── build.gradle.kts              # App-level build config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/commenteditor/app/
│       │   ├── MainActivity.kt       # UI & file saving logic
│       │   ├── CommentParser.kt      # Comment parsing engine
│       │   └── ui/theme/             # Compose theme files
│       └── res/                      # Icons, colors, strings
├── build.gradle.kts                  # Project-level build config
├── settings.gradle.kts
└── gradle.properties
```

## License

MIT License — free to use and modify.
