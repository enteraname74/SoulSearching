package com.github.enteraname74.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Folder

/**
 * Room representation of a Folder.
 */
@Entity
internal data class RoomFolder(
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