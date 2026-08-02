package com.commenteditor.app

object CommentParser {

    /**
     * Extracts the filename from the first non-empty, non-shebang comment line.
     * The comment must wrap the filename with its own markers.
     *
     * Supported formats:
     *   # main.py #           (Python, Shell, Ruby, YAML, etc.)
     *   // app.js //          (JavaScript, Java, Kotlin, C++, Go, Rust, etc.)
     *   /* main.c */          (C, C++, CSS, etc.)
     *   <!-- index.html -->   (HTML, XML)
     *   -- schema.sql --      (SQL, Lua, Haskell)
     *   ;; config.lisp ;;     (Lisp, Clojure, Scheme)
     *   REM batch.bat REM     (Batch)
     *   <# ps.ps1 #>         (PowerShell block)
     */
    fun extractFilename(code: String): String? {
        val firstLine = code.lineSequence()
            .firstOrNull { line ->
                line.isNotBlank() && !line.trimStart().startsWith("#!")
            }
            ?.trim()
            ?: return null

        return when {
            // Python, Shell, Ruby, YAML, etc: # filename.ext #
            firstLine.startsWith("#") && firstLine.endsWith("#") && firstLine.length > 2 -> {
                firstLine.removePrefix("#").removeSuffix("#").trim()
            }

            // C-style single line: // filename.ext //
            firstLine.startsWith("//") && firstLine.endsWith("//") && firstLine.length > 4 -> {
                firstLine.removePrefix("//").removeSuffix("//").trim()
            }

            // C-style multi-line: /* filename.ext */
            firstLine.startsWith("/*") && firstLine.endsWith("*/") && firstLine.length > 4 -> {
                firstLine.removePrefix("/*").removeSuffix("*/").trim()
            }

            // HTML / XML: <!-- filename.ext -->
            firstLine.startsWith("<!--") && firstLine.endsWith("-->") && firstLine.length > 7 -> {
                firstLine.removePrefix("<!--").removeSuffix("-->").trim()
            }

            // SQL / Lua / Haskell: -- filename.ext --
            firstLine.startsWith("--") && firstLine.endsWith("--") && firstLine.length > 4 -> {
                firstLine.removePrefix("--").removeSuffix("--").trim()
            }

            // Lisp family: ;; filename.ext ;;
            firstLine.startsWith(";;") && firstLine.endsWith(";;") && firstLine.length > 4 -> {
                firstLine.removePrefix(";;").removeSuffix(";;").trim()
            }

            // Windows Batch: REM filename.bat REM
            firstLine.startsWith("REM", ignoreCase = true) &&
            firstLine.endsWith("REM", ignoreCase = true) &&
            firstLine.length > 6 -> {
                firstLine.removePrefix("REM").removeSuffix("REM").trim()
            }

            // PowerShell block: <# filename.ps1 #>
            firstLine.startsWith("<#") && firstLine.endsWith("#>") && firstLine.length > 4 -> {
                firstLine.removePrefix("<#").removeSuffix("#>").trim()
            }

            else -> null
        }
    }
}
