package com.github.enteraname74.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
internal data class RoomArtist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var coverId: UUID? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)