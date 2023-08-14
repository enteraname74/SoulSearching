package com.github.soulsearching.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Playlist(
    @PrimaryKey
    val playlistId: UUID = UUID.randomUUID(),
    var name: String = "",
    var coverId: UUID? = null,
    val isFavorite: Boolean = false,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)