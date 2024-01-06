package com.github.enteraname74.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
internal data class RoomPlaylist(
    @PrimaryKey
    val playlistId: UUID = UUID.randomUUID(),
    var name: String = "",
    var coverId: UUID? = null,
    val isFavorite: Boolean = false,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)