package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import com.github.enteraname74.domain.model.Folder

/**
 * State for managing folders.
 */
sealed interface FolderState {
    data object Fetching: FolderState
    data object Saving: FolderState
    data class Data(val folders: List<Folder>): FolderState
}
