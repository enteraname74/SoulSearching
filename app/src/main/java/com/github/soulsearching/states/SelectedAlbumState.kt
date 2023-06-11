package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.AlbumWithMusics

data class SelectedAlbumState(
    val albumWithMusics: AlbumWithMusics = AlbumWithMusics(),
    val albumCover: Bitmap? = null,
    val hasSetNewCover: Boolean = false
)