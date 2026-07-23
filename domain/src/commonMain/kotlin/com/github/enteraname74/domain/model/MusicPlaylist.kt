package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

/**
 * Used to link a song to a playlist.
 */
data class MusicPlaylist(
    val musicId: Uuid = Uuid.random(),
    val playlistId: Uuid = Uuid.random()
) {
    val id: String
        get() = "$musicId$playlistId"
}
