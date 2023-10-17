package com.github.soulsearching.states

import com.github.soulsearching.classes.enumsAndTypes.FolderStateType
import com.github.soulsearching.database.model.Folder
import com.github.soulsearching.database.model.Music

data class FolderState(
    val state: FolderStateType = FolderStateType.WAITING_FOR_USER_ACTION,
    val folders: ArrayList<Folder> = ArrayList()
)
