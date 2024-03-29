package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * State for managing a selected album.
 */
data class SelectedAlbumState(
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val albumCover: Bitmap? = null,
    val hasSetNewCover: Boolean = false
)