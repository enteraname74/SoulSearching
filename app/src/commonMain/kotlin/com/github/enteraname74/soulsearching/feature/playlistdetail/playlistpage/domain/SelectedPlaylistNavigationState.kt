package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import kotlin.uuid.Uuid

sealed interface SelectedPlaylistNavigationState {
    data object Idle: SelectedPlaylistNavigationState
    data class ToModifyMusic(val musicId: Uuid): SelectedPlaylistNavigationState
    data class ToEdit(val playlistId: Uuid): SelectedPlaylistNavigationState
    data object Back : SelectedPlaylistNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : SelectedPlaylistNavigationState
}
