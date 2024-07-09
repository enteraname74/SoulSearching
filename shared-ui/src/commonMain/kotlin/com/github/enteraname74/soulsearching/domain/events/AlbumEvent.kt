package com.github.enteraname74.soulsearching.domain.events

import com.github.enteraname74.domain.model.Album
import java.util.UUID

/**
 * Events related to albums.
 */
sealed interface AlbumEvent {

    data class UpdateQuickAccessState(val album: Album): AlbumEvent
    data class DeleteAlbum(val albumId: UUID) : AlbumEvent
    data class SetSortDirection(val type: Int) : AlbumEvent
    data class SetSortType(val type: Int) : AlbumEvent
    data class BottomSheet(val isShown: Boolean) : AlbumEvent
    data class DeleteDialog(val isShown: Boolean) : AlbumEvent
    data class AddNbPlayed(val albumId: UUID): AlbumEvent

}