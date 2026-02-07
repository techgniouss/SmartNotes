package com.smartnotes.data.repository

import com.smartnotes.data.dao.FolderDao
import com.smartnotes.data.entities.Folder
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val folderDao: FolderDao) {
    fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()

    suspend fun getFolderById(folderId: Long): Folder? = folderDao.getFolderById(folderId)

    suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder)

    suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder)

    suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)

    suspend fun deleteFolderById(folderId: Long) = folderDao.deleteFolderById(folderId)
}
