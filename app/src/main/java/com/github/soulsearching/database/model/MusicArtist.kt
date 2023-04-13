package com.github.soulsearching.database.model

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["musicId", "artistId"])
data class MusicArtist(
    val musicId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID()
)