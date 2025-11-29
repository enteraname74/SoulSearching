package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import java.util.*

sealed interface SelectedFolderNavigationState {
    data object Idle : SelectedFolderNavigationState
    data class ToModifyMusic(val musicId: UUID) : SelectedFolderNavigationState
    data object Back: SelectedFolderNavigationState
}