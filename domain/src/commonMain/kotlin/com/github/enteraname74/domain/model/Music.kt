package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * Represent a song with information related to it.
 * It does not possess its cover directly.
 */
data class Music(
    val musicId: UUID = UUID.randomUUID(),
    var name: String = "",
    val album: String = "",
    val artist: String = "",
    var cover: Cover,
    var duration: Long = 0L,
    var path: String = "",
    var folder: String = "",
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    override var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false
): QuickAccessible