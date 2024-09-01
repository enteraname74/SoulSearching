package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import java.util.*

/**
 * State for managing a selected album.
 */
sealed interface SelectedAlbumState {
    data object Loading : SelectedAlbumState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val artistId: UUID?,
    ) : SelectedAlbumState
}