package com.github.enteraname74.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class Folder(
    @PrimaryKey
    val folderPath: String = "",
    val isSelected: Boolean = true
)