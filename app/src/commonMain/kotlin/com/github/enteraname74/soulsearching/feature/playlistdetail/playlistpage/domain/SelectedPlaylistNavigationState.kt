package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import java.util.*

sealed interface SelectedPlaylistNavigationState {
    data object Idle: SelectedPlaylistNavigationState
    data class ToModifyMusic(val musicId: UUID): SelectedPlaylistNavigationState
    data class ToEdit(val playlistId: UUID): SelectedPlaylistNavigationState
    data object Back : SelectedPlaylistNavigationState
}