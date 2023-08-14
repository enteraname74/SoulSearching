package com.github.soulsearching.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Album(
    @PrimaryKey
    val albumId: UUID = UUID.randomUUID(),
    var albumName: String = "",
    var coverId: UUID? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)