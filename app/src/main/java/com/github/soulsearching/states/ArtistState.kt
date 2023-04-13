package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Artist

data class ArtistState(
    val artists: List<Artist> = emptyList(),
    val selectedArtist : Artist = Artist(),
    val cover : Bitmap? = null,
    val name : String = ""
)