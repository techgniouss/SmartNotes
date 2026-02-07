package com.smartnotes.data.dao

import androidx.room.*
import com.smartnotes.data.entities.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY usageCount DESC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE name = :tagName")
    suspend fun getTag(tagName: String): Tag?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag)

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("UPDATE tags SET usageCount = usageCount + 1 WHERE name = :tagName")
    suspend fun incrementUsageCount(tagName: String)

    @Query("UPDATE tags SET usageCount = usageCount - 1 WHERE name = :tagName")
    suspend fun decrementUsageCount(tagName: String)
}
