package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ArtistWithMusics

data class ArtistState(
    val artists: List<ArtistWithMusics> = emptyList(),
    val selectedArtist : Artist = Artist(),
    val cover : Bitmap? = null,
    val name : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false
)