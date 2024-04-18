package com.github.soulsearching.modifyelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.domain.events.PlaylistEvent
import java.util.UUID

/**
 * Events for the modify playlist screen.
 */
sealed interface ModifyPlaylistEvent {
    data object UpdatePlaylist : ModifyPlaylistEvent
    data class PlaylistFromId(val playlistId: UUID) : ModifyPlaylistEvent
    data class SetCover(val cover: ImageBitmap) : ModifyPlaylistEvent
    data class SetName(val name: String) : ModifyPlaylistEvent
}