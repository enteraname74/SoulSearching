package com.github.soulsearching.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.domain.events.MusicEvent

/**
 * Event for the modify music screen.
 */
sealed interface ModifyMusicEvent {
    data object UpdateMusic : ModifyMusicEvent
    data class SetName(val name: String) : ModifyMusicEvent
    data class SetArtist(val artist: String) : ModifyMusicEvent
    data class SetAlbum(val album: String) : ModifyMusicEvent
    data class SetCover(val cover: ImageBitmap) : ModifyMusicEvent
}