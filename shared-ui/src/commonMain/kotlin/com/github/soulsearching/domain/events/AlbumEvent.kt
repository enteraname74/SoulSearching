package com.github.soulsearching.domain.events

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import java.util.UUID

/**
 * Events related to albums.
 */
sealed interface AlbumEvent {

    data object UpdateQuickAccessState: AlbumEvent
    data object DeleteAlbum : AlbumEvent
    data class SetSortDirection(val type: Int) : AlbumEvent
    data class SetSortType(val type: Int) : AlbumEvent
    data class SetSelectedAlbum(val albumWithArtist: AlbumWithArtist): AlbumEvent
    data class BottomSheet(val isShown: Boolean) : AlbumEvent
    data class DeleteDialog(val isShown: Boolean) : AlbumEvent
    data class AddNbPlayed(val albumId: UUID): AlbumEvent

}