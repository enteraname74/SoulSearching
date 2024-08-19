package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail

/**
 * State for managing a selected playlist.
 */
sealed interface SelectedPlaylistState {
    data object Loading : SelectedPlaylistState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val allPlaylists: List<PlaylistWithMusics>,
    ) : SelectedPlaylistState
}