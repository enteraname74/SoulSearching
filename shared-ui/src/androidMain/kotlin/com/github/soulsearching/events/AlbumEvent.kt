package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import java.util.UUID

/**
 * Events related to albums.
 */
sealed interface AlbumEvent {
    data object UpdateAlbum : AlbumEvent
    data object UpdateQuickAccessState: AlbumEvent
    data class AlbumFromID(val albumId: UUID) : AlbumEvent
    data object DeleteAlbum : AlbumEvent
    data class SetSortDirection(val type: Int) : AlbumEvent
    data class SetSortType(val type: Int) : AlbumEvent
    data class SetSelectedAlbum(val albumWithArtist: AlbumWithArtist): AlbumEvent
    data class SetName(val name: String) : AlbumEvent
    data class SetArtist(val artist: String) : AlbumEvent
    data class SetCover(val cover: Bitmap) : AlbumEvent
    data class BottomSheet(val isShown: Boolean) : AlbumEvent
    data class DeleteDialog(val isShown: Boolean) : AlbumEvent
    data class AddNbPlayed(val albumId: UUID): AlbumEvent

}