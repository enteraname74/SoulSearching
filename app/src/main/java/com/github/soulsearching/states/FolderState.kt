package com.github.soulsearching.states

import com.github.soulsearching.database.model.Folder
import com.github.soulsearching.database.model.Music

data class FolderState(
    val folders: ArrayList<Folder> = ArrayList()
)
