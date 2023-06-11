package com.github.soulsearching.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Artist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var coverId: UUID? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0
)