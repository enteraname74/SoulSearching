package com.github.soulsearching.states

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * State for managing a selected album.
 */
data class SelectedAlbumState(
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val albumCover: ImageBitmap? = null,
    val hasSetNewCover: Boolean = false
)