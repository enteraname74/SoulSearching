package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * Represent an Album and information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Album(
    val albumId: UUID = UUID.randomUUID(),
    var albumName: String = "",
    var cover: Cover? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
)