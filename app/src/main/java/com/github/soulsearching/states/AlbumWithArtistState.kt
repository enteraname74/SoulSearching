package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithArtist

data class AlbumWithArtistState(
    val albums: List<AlbumWithArtist> = emptyList(),
    val selectedAlbum : AlbumWithArtist = AlbumWithArtist(),
    val cover : Bitmap? = null,
    val albumName : String = "",
    val artistName : String = ""
)