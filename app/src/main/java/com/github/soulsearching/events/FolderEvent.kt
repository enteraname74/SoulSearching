package com.github.soulsearching.events

import com.github.soulsearching.classes.enumsAndTypes.FolderStateType
import com.github.soulsearching.database.model.Folder

interface FolderEvent {
    data class SetState(val newState: FolderStateType): FolderEvent
    data class SetSelectedFolder(val folder: Folder, val isSelected: Boolean): FolderEvent
    object SaveSelection: FolderEvent
}