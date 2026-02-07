package com.smartnotes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartnotes.data.dao.FolderDao
import com.smartnotes.data.dao.NoteDao
import com.smartnotes.data.dao.TagDao
import com.smartnotes.data.entities.Folder
import com.smartnotes.data.entities.Note
import com.smartnotes.data.entities.NoteFts
import com.smartnotes.data.entities.Tag

@Database(
    entities = [Note::class, NoteFts::class, Folder::class, Tag::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
