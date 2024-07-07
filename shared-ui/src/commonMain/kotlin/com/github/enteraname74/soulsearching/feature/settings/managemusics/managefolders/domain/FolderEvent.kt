package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.model.FolderStateType

/**
 * Events related to folders.
 */
sealed interface FolderEvent {
    data class SetState(val newState: FolderStateType): FolderEvent
    data class SetSelectedFolder(val folder: Folder, val isSelected: Boolean): FolderEvent
    data class SaveSelection(val updateProgress: (Float) -> Unit): FolderEvent
    data object FetchFolders: FolderEvent
}