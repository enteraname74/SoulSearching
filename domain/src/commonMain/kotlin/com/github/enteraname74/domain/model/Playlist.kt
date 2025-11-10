package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represent a playlist with information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Playlist(
    val playlistId: UUID = UUID.randomUUID(),
    val name: String = "",
    val cover: Cover? = null,
    val isFavorite: Boolean = false,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false
)