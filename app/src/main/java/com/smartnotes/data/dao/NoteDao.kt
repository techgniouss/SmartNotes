package com.smartnotes.data.dao

import androidx.room.*
import com.smartnotes.data.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?

    @Query("SELECT * FROM notes WHERE folderId = :folderId ORDER BY updatedAt DESC")
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%' ORDER BY updatedAt DESC")
    fun getNotesByTag(tag: String): Flow<List<Note>>

    @Query("""
        SELECT notes.* FROM notes 
        JOIN notes_fts ON notes.rowid = notes_fts.rowid 
        WHERE notes_fts MATCH :query
        ORDER BY updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNotesCount(): Int

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getAllNotesSortedByTitle(): Flow<List<Note>>
}
