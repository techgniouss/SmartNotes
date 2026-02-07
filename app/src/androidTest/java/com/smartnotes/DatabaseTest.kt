package com.smartnotes

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartnotes.data.database.AppDatabase
import com.smartnotes.data.entities.Folder
import com.smartnotes.data.entities.Note
import com.smartnotes.data.entities.Tag
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    
    private lateinit var database: AppDatabase
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun testInsertAndReadNote() = runBlocking {
        val note = Note(
            title = "Test Note",
            content = "This is a test note",
            tags = listOf("test", "android")
        )
        
        val noteId = database.noteDao().insertNote(note)
        val retrievedNote = database.noteDao().getNoteById(noteId)
        
        assertNotNull(retrievedNote)
        assertEquals("Test Note", retrievedNote?.title)
        assertEquals("This is a test note", retrievedNote?.content)
        assertEquals(2, retrievedNote?.tags?.size)
    }
    
    @Test
    fun testInsertAndReadFolder() = runBlocking {
        val folder = Folder(name = "Work")
        
        val folderId = database.folderDao().insertFolder(folder)
        val retrievedFolder = database.folderDao().getFolderById(folderId)
        
        assertNotNull(retrievedFolder)
        assertEquals("Work", retrievedFolder?.name)
    }
    
    @Test
    fun testInsertAndReadTag() = runBlocking {
        val tag = Tag(name = "important", usageCount = 5)
        
        database.tagDao().insertTag(tag)
        val retrievedTag = database.tagDao().getTag("important")
        
        assertNotNull(retrievedTag)
        assertEquals("important", retrievedTag?.name)
        assertEquals(5, retrievedTag?.usageCount)
    }
    
    @Test
    fun testGetNotesByFolder() = runBlocking {
        val folder = Folder(name = "Personal")
        val folderId = database.folderDao().insertFolder(folder)
        
        val note1 = Note(title = "Note 1", content = "Content 1", folderId = folderId)
        val note2 = Note(title = "Note 2", content = "Content 2", folderId = folderId)
        
        database.noteDao().insertNote(note1)
        database.noteDao().insertNote(note2)
        
        val notes = database.noteDao().getNotesByFolder(folderId).first()
        
        assertEquals(2, notes.size)
    }
    
    @Test
    fun testDeleteNote() = runBlocking {
        val note = Note(title = "Delete Me", content = "This will be deleted")
        val noteId = database.noteDao().insertNote(note)
        
        database.noteDao().deleteNoteById(noteId)
        val retrievedNote = database.noteDao().getNoteById(noteId)
        
        assertNull(retrievedNote)
    }
    
    @Test
    fun testUpdateNote() = runBlocking {
        val note = Note(title = "Original", content = "Original content")
        val noteId = database.noteDao().insertNote(note)
        
        val updatedNote = note.copy(id = noteId, title = "Updated", content = "Updated content")
        database.noteDao().updateNote(updatedNote)
        
        val retrievedNote = database.noteDao().getNoteById(noteId)
        assertEquals("Updated", retrievedNote?.title)
        assertEquals("Updated content", retrievedNote?.content)
    }
}
