package com.smartnotes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey
    val name: String,
    val usageCount: Int = 0
)
