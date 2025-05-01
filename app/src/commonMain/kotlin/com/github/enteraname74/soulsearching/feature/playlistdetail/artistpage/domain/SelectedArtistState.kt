package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected artist.
 */
sealed interface SelectedArtistState {
    data object Error: SelectedArtistState
    data object Loading : SelectedArtistState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val artistAlbums: List<AlbumWithMusics>,
    ): SelectedArtistState
}