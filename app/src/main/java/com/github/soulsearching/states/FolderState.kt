package com.github.soulsearching.states

import com.github.enteraname74.domain.model.Folder
import com.github.soulsearching.classes.enumsAndTypes.FolderStateType

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
