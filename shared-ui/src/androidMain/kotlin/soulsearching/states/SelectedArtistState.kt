package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
     * State for managing a selected artist.
 */
data class SelectedArtistState(
    val artistWithMusics : ArtistWithMusics = ArtistWithMusics(),
    val hasCoverBeenChanged : Boolean = false,
    val cover : Bitmap? = null
)