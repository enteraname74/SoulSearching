package com.github.soulsearching.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * Events for the modify album screen.
 */
sealed interface ModifyAlbumEvent {
    data object UpdateAlbum : ModifyAlbumEvent
    data class AlbumFromID(val albumId: UUID) : ModifyAlbumEvent
    data class SetName(val name: String) : ModifyAlbumEvent
    data class SetArtist(val artist: String) : ModifyAlbumEvent
    data class SetCover(val cover: ImageBitmap) : ModifyAlbumEvent
    data class SetMatchingAlbums(val search: String) : ModifyAlbumEvent
    data class SetMatchingArtists(val search: String) : ModifyAlbumEvent

}