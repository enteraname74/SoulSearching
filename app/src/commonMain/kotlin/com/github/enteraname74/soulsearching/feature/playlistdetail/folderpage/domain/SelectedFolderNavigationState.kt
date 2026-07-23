package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import kotlin.uuid.Uuid

sealed interface SelectedFolderNavigationState {
    data object Idle : SelectedFolderNavigationState
    data class ToModifyMusic(val musicId: Uuid) : SelectedFolderNavigationState
    data object Back: SelectedFolderNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : SelectedFolderNavigationState
}
