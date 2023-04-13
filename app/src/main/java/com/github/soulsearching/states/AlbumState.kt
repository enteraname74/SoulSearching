package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Album

data class AlbumState(
    val albums: List<Album> = emptyList(),
    val selectedAlbum : Album = Album(),
    val cover : Bitmap? = null,
    val name : String = "",
    val artist : String = ""
)