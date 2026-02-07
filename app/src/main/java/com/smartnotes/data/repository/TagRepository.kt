package com.smartnotes.data.repository

import com.smartnotes.data.dao.TagDao
import com.smartnotes.data.entities.Tag
import kotlinx.coroutines.flow.Flow

class TagRepository(private val tagDao: TagDao) {
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    suspend fun getTag(tagName: String): Tag? = tagDao.getTag(tagName)

    suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)

    suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
}
