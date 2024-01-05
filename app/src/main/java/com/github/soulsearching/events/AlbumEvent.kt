package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.AlbumWithArtist
import java.util.*

/**
 * Events related to albums.
 */
sealed interface AlbumEvent {
    object UpdateAlbum : AlbumEvent
    object UpdateQuickAccessState: AlbumEvent
    data class AlbumFromID(val albumId: UUID) : AlbumEvent
    object DeleteAlbum : AlbumEvent
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