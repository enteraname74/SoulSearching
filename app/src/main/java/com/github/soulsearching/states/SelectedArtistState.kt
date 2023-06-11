package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.ArtistWithMusics

data class SelectedArtistState(
    val artistWithMusics : ArtistWithMusics = ArtistWithMusics(),
    val hasCoverBeenChanged : Boolean = false,
    val cover : Bitmap? = null
)