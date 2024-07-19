package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Playlist

sealed interface AllPlaylistsNavigationState {
    data object Idle: AllPlaylistsNavigationState
    data class ToModifyPlaylist(val selectedPlaylist: Playlist): AllPlaylistsNavigationState
}