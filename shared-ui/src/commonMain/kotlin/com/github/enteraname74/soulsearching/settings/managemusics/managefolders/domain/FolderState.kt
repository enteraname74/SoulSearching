package com.github.soulsearching.settings.managemusics.managefolders.domain

import com.github.enteraname74.domain.model.Folder
import com.github.soulsearching.settings.managemusics.managefolders.domain.model.FolderStateType

/**
 * State for managing folders.
 */
data class FolderState(
    /**
     * Current operation on the folders.
     */
    val state: FolderStateType = FolderStateType.FETCHING_FOLDERS,
    val folders: ArrayList<Folder> = ArrayList()
)
