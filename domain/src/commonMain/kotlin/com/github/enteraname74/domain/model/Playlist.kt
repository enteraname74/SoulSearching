package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represent a playlist with information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Playlist(
    val playlistId: UUID = UUID.randomUUID(),
    var name: String = "",
    var cover: Cover? = null,
    val isFavorite: Boolean = false,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    val dataMode: DataMode,
)