package com.smartnotes.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smartnotes.ui.components.DrawingCanvas
import com.smartnotes.ui.components.MathResultCard
import com.smartnotes.viewmodel.NoteEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    noteId: Long?,
    onNavigateBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val isDrawingMode by viewModel.isDrawingMode.collectAsState()
    val drawingPaths by viewModel.drawingPaths.collectAsState()
    val mathResults by viewModel.mathResults.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    
    val context = LocalContext.current
    var showTagDialog by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceInput()
        }
    }

    LaunchedEffect(noteId) {
        if (noteId != null && noteId > 0) {
            viewModel.loadNote(noteId)
        }
    }

    LaunchedEffect(saveStatus) {
        if (saveStatus is NoteEditorViewModel.SaveStatus.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId != null && noteId > 0) "Edit Note" else "New Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    ) {
                        Icon(
                            if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    
                    IconButton(onClick = { viewModel.toggleDrawingMode() }) {
                        Icon(
                            if (isDrawingMode) Icons.Default.Edit else Icons.Default.Draw,
                            contentDescription = "Drawing Mode",
                            tint = if (isDrawingMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export") },
                            onClick = {
                                showMoreMenu = false
                                showExportMenu = true
                            },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Tags") },
                            onClick = {
                                showMoreMenu = false
                                showTagDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Label, null) }
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export as PDF") },
                            onClick = {
                                viewModel.exportToPdf()
                                showExportMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as Text") },
                            onClick = {
                                viewModel.exportToText()
                                showExportMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as Image") },
                            onClick = {
                                viewModel.exportToImage()
                                showExportMenu = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isDrawingMode) {
                        IconButton(onClick = { viewModel.clearDrawing() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Canvas")
                        }
                        IconButton(onClick = { viewModel.undoDrawing() }) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo")
                        }
                        IconButton(onClick = { viewModel.recognizeHandwriting() }) {
                            Icon(Icons.Default.TextFields, contentDescription = "Recognize")
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { viewModel.saveNote() },
                        enabled = saveStatus !is NoteEditorViewModel.SaveStatus.Saving
                    ) {
                        if (saveStatus is NoteEditorViewModel.SaveStatus.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isDrawingMode) {
                DrawingCanvas(
                    paths = drawingPaths,
                    onPathAdded = { viewModel.addDrawingPath(it) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.setTitle(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Note Title") },
                        textStyle = MaterialTheme.typography.titleLarge,
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = content,
                        onValueChange = { viewModel.setContent(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        placeholder = { Text("Start typing your note...") },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (selectedTags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedTags.forEach { tag ->
                                AssistChip(
                                    onClick = { viewModel.removeTag(tag) },
                                    label = { Text(tag) },
                                    trailingIcon = {
                                        Icon(Icons.Default.Close, contentDescription = "Remove")
                                    }
                                )
                            }
                        }
                    }
                    
                    if (mathResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Math Results",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        mathResults.forEach { (expression, result) ->
                            MathResultCard(
                                expression = expression,
                                result = result
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTagDialog) {
        AddTagDialog(
            onDismiss = { showTagDialog = false },
            onConfirm = { tag ->
                viewModel.addTag(tag)
                showTagDialog = false
            }
        )
    }
}

@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tag") },
        text = {
            OutlinedTextField(
                value = tagName,
                onValueChange = { tagName = it },
                label = { Text("Tag Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (tagName.isNotBlank()) onConfirm(tagName) },
                enabled = tagName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
