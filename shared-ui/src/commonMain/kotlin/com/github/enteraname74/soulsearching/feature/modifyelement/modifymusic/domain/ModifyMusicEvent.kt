package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Event for the modify music screen.
 */
sealed interface ModifyMusicEvent {
    data object UpdateMusic : ModifyMusicEvent
    data class SetName(val name: String) : ModifyMusicEvent
    data class SetArtist(val artist: String) : ModifyMusicEvent
    data class SetAlbum(val album: String) : ModifyMusicEvent
    data class SetCover(val cover: ImageBitmap) : ModifyMusicEvent
    data class SetMatchingAlbums(val search: String) : ModifyMusicEvent
    data class SetMatchingArtists(val search: String) : ModifyMusicEvent
}