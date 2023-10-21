package com.github.soulsearching.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    @PrimaryKey
    val folderPath: String = "",
    val isSelected: Boolean = true
)