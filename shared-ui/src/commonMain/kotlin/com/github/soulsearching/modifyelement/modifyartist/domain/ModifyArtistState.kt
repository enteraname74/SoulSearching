package com.github.soulsearching.modifyelement.modifyartist.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
 * UI state of the modify artist screen.
 */
data class ModifyArtistState(
    val artistWithMusics : ArtistWithMusics = ArtistWithMusics(),
    val hasCoverBeenChanged : Boolean = false,
    val cover : ImageBitmap? = null,
    val matchingArtistsNames: List<String> = emptyList()
)
