package com.github.soulsearching.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music

/**
 * UI state of the modify music screen.
 */
data class ModifyMusicState(
    val isSelectedMusicFetched: Boolean = false,
    val selectedMusic: Music = Music(),
    val modifiedMusicInformation: Music = Music(),
    val hasCoverBeenChanged: Boolean = false,
    val cover: ImageBitmap? = null
)
