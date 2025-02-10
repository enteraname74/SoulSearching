package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected playlist.
 */
sealed interface SelectedPlaylistState {
    data object Error: SelectedPlaylistState
    data object Loading : SelectedPlaylistState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val selectedPlaylist: Playlist,
    ) : SelectedPlaylistState
}