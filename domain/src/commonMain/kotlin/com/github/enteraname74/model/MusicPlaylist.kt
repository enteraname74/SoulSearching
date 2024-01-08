package com.github.enteraname74.model

import java.util.UUID

/**
 * Used to link a song to a playlist.
 */
data class MusicPlaylist(
    val id: Long = 0,
    val musicId: UUID = UUID.randomUUID(),
    val playlistId: UUID = UUID.randomUUID()
)