package com.github.soulsearching.elementpage.albumpage.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * State for managing a selected album.
 */
data class SelectedAlbumState(
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isDeleteMusicDialogShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false
)