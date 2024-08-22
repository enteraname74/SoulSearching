package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import com.github.enteraname74.domain.model.Music

sealed interface SelectedFolderNavigationState {
    data object Idle : SelectedFolderNavigationState
    data class ToModifyMusic(val music: Music) : SelectedFolderNavigationState
}