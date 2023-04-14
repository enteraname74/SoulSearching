package com.github.soulsearching.database.model

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumArtist(
    val albumId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID()
)