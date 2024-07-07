package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * UI state of the modify album screen.
 */
data class ModifyAlbumState(
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val hasSetNewCover: Boolean = false,
    val albumCover: ImageBitmap? = null,
    val matchingAlbumsNames: List<String> = emptyList(),
    val matchingArtistsNames: List<String> = emptyList()
)
