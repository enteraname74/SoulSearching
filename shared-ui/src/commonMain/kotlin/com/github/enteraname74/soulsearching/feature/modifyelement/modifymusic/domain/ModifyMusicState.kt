package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music

/**
 * UI state of the modify music screen.
 */
data class ModifyMusicState(
    val selectedMusic: Music = Music(),
    val modifiedMusicInformation: Music = Music(),
    val hasCoverBeenChanged: Boolean = false,
    val cover: ImageBitmap? = null,
    val matchingAlbumsNames: List<String> = emptyList(),
    val matchingArtistsNames: List<String> = emptyList()
)
