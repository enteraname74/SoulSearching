package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.Folder

/**
 * Room representation of a Folder.
 */
@Entity
data class RoomFolder(
    @PrimaryKey
    val folderPath: String = "",
    val isSelected: Boolean = true
)

/**
 * Converts a RoomFolder to a Folder.
 */
internal fun RoomFolder.toFolder(): Folder = Folder(
    folderPath = folderPath,
    isSelected = isSelected
)

/**
 * Converts a Folder to a RoomFolder.
 */
internal fun Folder.toRoomFolder(): RoomFolder = RoomFolder(
    folderPath = folderPath,
    isSelected = isSelected
)
