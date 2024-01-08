package com.github.soulsearching.events

import com.github.enteraname74.model.Folder
import com.github.soulsearching.classes.types.FolderStateType

/**
 * Events related to folders.
 */
sealed interface FolderEvent {
    data class SetState(val newState: FolderStateType): FolderEvent
    data class SetSelectedFolder(val folder: Folder, val isSelected: Boolean): FolderEvent
    data class SaveSelection(val updateProgress: (Float) -> Unit): FolderEvent
    data object FetchFolders: FolderEvent
}