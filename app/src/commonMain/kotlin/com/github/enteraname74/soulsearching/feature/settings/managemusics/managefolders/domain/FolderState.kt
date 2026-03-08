package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain

import com.github.enteraname74.domain.model.Folder

/**
 * State for managing folders.
 */
data class FolderState(
    val folders: List<Folder> = emptyList(),
)
