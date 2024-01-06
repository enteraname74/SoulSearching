package com.github.enteraname74.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity
internal data class Music(
    @PrimaryKey
    val musicId: UUID = UUID.randomUUID(),
    var name: String = "",
    val album: String = "",
    val artist: String = "",
    var coverId: UUID? = null,
    var duration: Long = 0L,
    var path: String = "",
    var folder: String = "",
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false
)