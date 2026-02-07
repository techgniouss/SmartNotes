package com.smartnotes.viewmodel

import android.content.Context
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.digitalink.Ink
import com.smartnotes.data.entities.Note
import com.smartnotes.data.repository.NoteRepository
import com.smartnotes.services.ExportService
import com.smartnotes.services.HandwritingRecognitionService
import com.smartnotes.services.MathProcessingService
import com.smartnotes.services.VoiceInputService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class NoteEditorViewModel(
    private val noteRepository: NoteRepository,
    private val context: Context
) : ViewModel() {

    private val voiceInputService = VoiceInputService(context)
    private val handwritingService = HandwritingRecognitionService(context)
    private val mathService = MathProcessingService()
    private val exportService = ExportService(context)

    private val _note = MutableStateFlow<Note?>(null)
    val note = _note.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags = _selectedTags.asStateFlow()

    private val _selectedFolderId = MutableStateFlow<Long?>(null)
    val selectedFolderId = _selectedFolderId.asStateFlow()

    private val _isDrawingMode = MutableStateFlow(false)
    val isDrawingMode = _isDrawingMode.asStateFlow()

    private val _drawingPaths = MutableStateFlow<List<Path>>(emptyList())
    val drawingPaths = _drawingPaths.asStateFlow()

    private val _mathResults = MutableStateFlow<Map<String, String>>(emptyMap())
    val mathResults = _mathResults.asStateFlow()

    val isListening = voiceInputService.isListening
    val recognizedText = voiceInputService.recognizedText
    val voiceError = voiceInputService.error

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus = _saveStatus.asStateFlow()

    init {
        viewModelScope.launch {
            handwritingService.initialize()
        }

        viewModelScope.launch {
            recognizedText.collect { text ->
                if (text.isNotEmpty()) {
                    val parsedMath = mathService.parseSpokenMath(text)
                    appendContent(parsedMath)
                }
            }
        }
    }

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val loadedNote = noteRepository.getNoteById(noteId)
            loadedNote?.let {
                _note.value = it
                _title.value = it.title
                _content.value = it.content
                _selectedTags.value = it.tags
                _selectedFolderId.value = it.folderId
                processMathExpressions()
            }
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
        processMathExpressions()
    }

    fun appendContent(text: String) {
        _content.value = _content.value + (if (_content.value.isEmpty()) "" else " ") + text
        processMathExpressions()
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_selectedTags.value.contains(tag)) {
            _selectedTags.value = _selectedTags.value + tag
        }
    }

    fun removeTag(tag: String) {
        _selectedTags.value = _selectedTags.value - tag
    }

    fun setFolderId(folderId: Long?) {
        _selectedFolderId.value = folderId
    }

    fun toggleDrawingMode() {
        _isDrawingMode.value = !_isDrawingMode.value
    }

    fun addDrawingPath(path: Path) {
        _drawingPaths.value = _drawingPaths.value + path
    }

    fun clearDrawing() {
        _drawingPaths.value = emptyList()
    }

    fun undoDrawing() {
        if (_drawingPaths.value.isNotEmpty()) {
            _drawingPaths.value = _drawingPaths.value.dropLast(1)
        }
    }

    fun recognizeHandwriting() {
        viewModelScope.launch {
            val inkBuilder = Ink.builder()
            val strokeBuilder = Ink.Stroke.builder()
            
            _drawingPaths.value.forEach { path ->
            }
            
            val ink = inkBuilder.build()
            val recognizedText = handwritingService.recognizeInk(ink)
            recognizedText?.let { appendContent(it) }
        }
    }

    fun startVoiceInput() {
        voiceInputService.startListening()
    }

    fun stopVoiceInput() {
        voiceInputService.stopListening()
    }

    private fun processMathExpressions() {
        val expressions = mathService.extractMathExpressions(_content.value)
        val results = mutableMapOf<String, String>()
        
        expressions.forEach { expr ->
            when (val result = mathService.solveExpression(expr)) {
                is MathProcessingService.Result.Success -> {
                    results[expr] = result.formatted
                }
                is MathProcessingService.Result.Error -> {
                }
            }
        }
        
        _mathResults.value = results
    }

    fun saveNote() {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            try {
                val mathExpressions = mathService.extractMathExpressions(_content.value)
                val noteToSave = if (_note.value != null) {
                    _note.value!!.copy(
                        title = _title.value.ifBlank { "Untitled Note" },
                        content = _content.value,
                        tags = _selectedTags.value,
                        folderId = _selectedFolderId.value,
                        hasMath = mathExpressions.isNotEmpty(),
                        mathExpressions = mathExpressions,
                        hasDrawing = _drawingPaths.value.isNotEmpty()
                    )
                } else {
                    Note(
                        title = _title.value.ifBlank { "Untitled Note" },
                        content = _content.value,
                        tags = _selectedTags.value,
                        folderId = _selectedFolderId.value,
                        hasMath = mathExpressions.isNotEmpty(),
                        mathExpressions = mathExpressions,
                        hasDrawing = _drawingPaths.value.isNotEmpty()
                    )
                }

                if (_note.value != null) {
                    noteRepository.updateNote(noteToSave)
                } else {
                    val newId = noteRepository.insertNote(noteToSave)
                    _note.value = noteToSave.copy(id = newId)
                }
                
                _saveStatus.value = SaveStatus.Success
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Failed to save note")
            }
        }
    }

    fun exportToPdf(): File? {
        val currentNote = _note.value ?: return null
        return exportService.exportToPdf(currentNote)
    }

    fun exportToText(): File? {
        val currentNote = _note.value ?: return null
        return exportService.exportToText(currentNote)
    }

    fun exportToImage(): File? {
        val currentNote = _note.value ?: return null
        return exportService.exportToImage(currentNote)
    }

    override fun onCleared() {
        super.onCleared()
        voiceInputService.destroy()
        handwritingService.release()
    }

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}
