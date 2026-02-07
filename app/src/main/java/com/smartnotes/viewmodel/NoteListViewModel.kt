package com.smartnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartnotes.data.entities.Folder
import com.smartnotes.data.entities.Note
import com.smartnotes.data.entities.Tag
import com.smartnotes.data.repository.FolderRepository
import com.smartnotes.data.repository.NoteRepository
import com.smartnotes.data.repository.TagRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFolder = MutableStateFlow<Folder?>(null)
    val selectedFolder = _selectedFolder.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

    private val _sortByTitle = MutableStateFlow(false)
    val sortByTitle = _sortByTitle.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(
        searchQuery,
        selectedFolder,
        selectedTag,
        sortByTitle
    ) { query, folder, tag, byTitle ->
        when {
            query.isNotEmpty() -> noteRepository.searchNotes(query)
            folder != null -> noteRepository.getNotesByFolder(folder.id)
            tag != null -> noteRepository.getNotesByTag(tag)
            byTitle -> noteRepository.getAllNotesSortedByTitle()
            else -> noteRepository.getAllNotes()
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val folders: StateFlow<List<Folder>> = folderRepository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFolder(folder: Folder?) {
        _selectedFolder.value = folder
        _selectedTag.value = null
    }

    fun selectTag(tag: String?) {
        _selectedTag.value = tag
        _selectedFolder.value = null
    }

    fun toggleSortOrder() {
        _sortByTitle.value = !_sortByTitle.value
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            folderRepository.insertFolder(Folder(name = name))
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.deleteFolder(folder)
        }
    }

    fun renameFolder(folder: Folder, newName: String) {
        viewModelScope.launch {
            folderRepository.updateFolder(folder.copy(name = newName))
        }
    }
}
