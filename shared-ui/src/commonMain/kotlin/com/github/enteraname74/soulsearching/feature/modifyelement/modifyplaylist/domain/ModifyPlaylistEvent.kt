package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
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