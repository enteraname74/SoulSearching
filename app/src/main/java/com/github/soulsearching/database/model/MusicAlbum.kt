package com.github.soulsearching.database.model

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["musicId", "albumId"])
data class MusicAlbum(
    val musicId: UUID = UUID.randomUUID(),
    val albumId: UUID = UUID.randomUUID()
)