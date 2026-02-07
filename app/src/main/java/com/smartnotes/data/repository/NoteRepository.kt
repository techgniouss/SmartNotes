package com.smartnotes.data.repository

import com.smartnotes.data.dao.NoteDao
import com.smartnotes.data.dao.TagDao
import com.smartnotes.data.entities.Note
import com.smartnotes.data.entities.Tag
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getAllNotesSortedByTitle(): Flow<List<Note>> = noteDao.getAllNotesSortedByTitle()

    suspend fun getNoteById(noteId: Long): Note? = noteDao.getNoteById(noteId)

    fun getNotesByFolder(folderId: Long): Flow<List<Note>> = noteDao.getNotesByFolder(folderId)

    fun getNotesByTag(tag: String): Flow<List<Note>> = noteDao.getNotesByTag(tag)

    fun searchNotes(query: String): Flow<List<Note>> {
        val formattedQuery = query.split(" ").joinToString(" OR ")
        return noteDao.searchNotes(formattedQuery)
    }

    suspend fun insertNote(note: Note): Long {
        val noteId = noteDao.insertNote(note)
        note.tags.forEach { tag ->
            val existingTag = tagDao.getTag(tag)
            if (existingTag != null) {
                tagDao.incrementUsageCount(tag)
            } else {
                tagDao.insertTag(Tag(tag, 1))
            }
        }
        return noteId
    }

    suspend fun updateNote(note: Note) {
        val oldNote = noteDao.getNoteById(note.id)
        oldNote?.let {
            val removedTags = it.tags - note.tags.toSet()
            val addedTags = note.tags.toSet() - it.tags

            removedTags.forEach { tag ->
                tagDao.decrementUsageCount(tag)
            }

            addedTags.forEach { tag ->
                val existingTag = tagDao.getTag(tag)
                if (existingTag != null) {
                    tagDao.incrementUsageCount(tag)
                } else {
                    tagDao.insertTag(Tag(tag, 1))
                }
            }
        }

        noteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteNote(note: Note) {
        note.tags.forEach { tag ->
            tagDao.decrementUsageCount(tag)
        }
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(noteId: Long) {
        val note = noteDao.getNoteById(noteId)
        note?.let { deleteNote(it) }
    }

    suspend fun getNotesCount(): Int = noteDao.getNotesCount()
}
