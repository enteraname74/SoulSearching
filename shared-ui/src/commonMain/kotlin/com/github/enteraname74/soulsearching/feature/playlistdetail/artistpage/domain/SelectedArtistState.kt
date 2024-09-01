package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected artist.
 */
sealed interface SelectedArtistState {
    data object Loading : SelectedArtistState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val artistAlbums: List<Album>,
    ): SelectedArtistState
}