package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected album.
 */
sealed interface SelectedAlbumState {
    data object Error: SelectedAlbumState
    data object Loading : SelectedAlbumState
    data class Data(
        val playlistDetail: PlaylistDetail,
    ) : SelectedAlbumState
}