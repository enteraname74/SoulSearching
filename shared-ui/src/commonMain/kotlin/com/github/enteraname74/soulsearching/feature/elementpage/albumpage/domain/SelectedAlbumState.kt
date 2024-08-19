package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail
import java.util.*

/**
 * State for managing a selected album.
 */
sealed interface SelectedAlbumState {
    data object Loading : SelectedAlbumState
    data class Data(
        val playlistDetail: PlaylistDetail,
        val allPlaylists: List<PlaylistWithMusics>,
        val artistId: UUID?,
    ) : SelectedAlbumState
}