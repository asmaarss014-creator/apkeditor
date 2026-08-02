package com.commenteditor.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.commenteditor.app.ui.theme.CommentEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommentEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedDirectoryUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPath by remember { mutableStateOf("No directory selected") }
    var codeText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var statusColor by remember { mutableStateOf(Color.Gray) }

    val detectedFilename = remember(codeText) {
        CommentParser.extractFilename(codeText) ?: "No filename detected in first comment"
    }
    val hasValidFilename = remember(codeText) {
        CommentParser.extractFilename(codeText) != null
    }

    val directoryPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                selectedDirectoryUri = uri
                selectedPath = uri.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comment Code Editor") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Directory Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Save Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedPath,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            directoryPicker.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Directory")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Code Editor Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Code Editor",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = { codeText = "" }) {
                            Text("Clear")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = codeText,
                        onValueChange = { codeText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        ),
                        placeholder = {
                            Text(
                                "Paste your code here...\nFirst comment must contain filename",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detected: $detectedFilename",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasValidFilename)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Execute Button
            Button(
                onClick = {
                    executeSave(context, selectedDirectoryUri, codeText, selectedPath) { msg, color ->
                        statusMessage = msg
                        statusColor = color
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDirectoryUri != null && codeText.isNotBlank() && hasValidFilename
            ) {
                Text("Execute & Save", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Card
            if (statusMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = statusMessage,
                        modifier = Modifier.padding(16.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How to use:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Select a directory where files will be saved\n" +
                               "2. Paste your code in the editor\n" +
                               "3. First comment must contain filename:\n" +
                               "   \u2022 Python:    # main.py #\n" +
                               "   \u2022 JS/Java:   // app.js //\n" +
                               "   \u2022 C/C++:     /* main.c */\n" +
                               "   \u2022 HTML:      <!-- index.html -->\n" +
                               "   \u2022 SQL/Lua:   -- schema.sql --\n" +
                               "   \u2022 Lisp:      ;; config.lisp ;;\n" +
                               "4. Click Execute & Save",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

private fun executeSave(
    context: android.content.Context,
    directoryUri: Uri?,
    code: String,
    pathDisplay: String,
    onStatus: (String, Color) -> Unit
) {
    if (directoryUri == null) {
        onStatus("Error: No directory selected", Color.Red)
        return
    }
    if (code.isBlank()) {
        onStatus("Error: No code to save", Color.Red)
        return
    }

    val filename = CommentParser.extractFilename(code)
    if (filename == null) {
        onStatus(
            "Error: Could not find filename in first comment\n" +
            "Examples: # main.py #  |  // app.js //  |  /* main.c */",
            Color.Red
        )
        return
    }

    try {
        val tree = DocumentFile.fromTreeUri(context, directoryUri)
        if (tree == null || !tree.canWrite()) {
            onStatus("Error: Cannot write to selected directory", Color.Red)
            return
        }

        // Delete existing file if present
        tree.findFile(filename)?.delete()

        // Create new file with appropriate MIME type
        val mimeType = getMimeType(filename)
        val newFile = tree.createFile(mimeType, filename)

        if (newFile != null && newFile.uri != null) {
            context.contentResolver.openOutputStream(newFile.uri)?.use { outputStream ->
                outputStream.write(code.toByteArray(Charsets.UTF_8))
            }
            onStatus(
                "\u2705 Success! Saved as: $filename",
                Color(0xFF2E7D32)
            )
        } else {
            onStatus("Error: Failed to create file", Color.Red)
        }
    } catch (e: Exception) {
        onStatus("Error: ${e.message}", Color.Red)
    }
}

private fun getMimeType(filename: String): String {
    return when {
        filename.endsWith(".py") -> "text/x-python"
        filename.endsWith(".js") -> "application/javascript"
        filename.endsWith(".java") -> "text/x-java-source"
        filename.endsWith(".kt") -> "text/x-kotlin"
        filename.endsWith(".c") -> "text/x-c"
        filename.endsWith(".cpp") || filename.endsWith(".cc") -> "text/x-c++"
        filename.endsWith(".h") || filename.endsWith(".hpp") -> "text/x-c-header"
        filename.endsWith(".html") || filename.endsWith(".htm") -> "text/html"
        filename.endsWith(".css") -> "text/css"
        filename.endsWith(".xml") -> "application/xml"
        filename.endsWith(".json") -> "application/json"
        filename.endsWith(".md") -> "text/markdown"
        filename.endsWith(".sh") -> "application/x-sh"
        filename.endsWith(".sql") -> "application/sql"
        filename.endsWith(".php") -> "application/x-php"
        filename.endsWith(".rb") -> "application/x-ruby"
        filename.endsWith(".go") -> "text/x-go"
        filename.endsWith(".rs") -> "text/x-rust"
        filename.endsWith(".swift") -> "text/x-swift"
        filename.endsWith(".dart") -> "application/dart"
        filename.endsWith(".ts") -> "application/typescript"
        filename.endsWith(".txt") -> "text/plain"
        else -> "text/plain"
    }
}
