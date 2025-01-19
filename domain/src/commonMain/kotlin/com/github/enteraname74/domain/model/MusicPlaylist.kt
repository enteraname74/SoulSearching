package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Used to link a song to a playlist.
 */
data class MusicPlaylist(
    val musicId: UUID = UUID.randomUUID(),
    val playlistId: UUID = UUID.randomUUID(),
    val dataMode: DataMode,
) {
    val id: String
        get() = "$musicId$playlistId"
}