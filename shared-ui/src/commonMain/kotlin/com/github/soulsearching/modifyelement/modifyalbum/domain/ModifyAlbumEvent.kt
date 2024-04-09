package com.github.soulsearching.modifyelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.domain.events.AlbumEvent
import java.util.UUID

/**
 * Events for the modify album screen.
 */
interface ModifyAlbumEvent {
    data object UpdateAlbum : ModifyAlbumEvent
    data class AlbumFromID(val albumId: UUID) : ModifyAlbumEvent
    data class SetName(val name: String) : ModifyAlbumEvent
    data class SetArtist(val artist: String) : ModifyAlbumEvent
    data class SetCover(val cover: ImageBitmap) : ModifyAlbumEvent

}