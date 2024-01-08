package com.github.soulsearching.states

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
     * State for managing a selected artist.
 */
data class SelectedArtistState(
    val artistWithMusics : ArtistWithMusics = ArtistWithMusics(),
    val hasCoverBeenChanged : Boolean = false,
    val cover : ImageBitmap? = null
)