package com.github.soulsearching.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * UI state of the modify album screen.
 */
data class ModifyAlbumState(
    val isSelectedAlbumFetched: Boolean = false,
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val hasSetNewCover: Boolean = false,
    val albumCover: ImageBitmap? = null
)
