package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import com.github.enteraname74.domain.model.Music

sealed interface SelectedPlaylistNavigationState {
    data object Idle: SelectedPlaylistNavigationState
    data class ToModifyMusic(val music: Music): SelectedPlaylistNavigationState
    data object ToEdit: SelectedPlaylistNavigationState
}