package com.github.soulsearching.database.model

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["musicId", "albumId"])
data class MusicAlbum(
    val musicId: UUID,
    val albumId: UUID
)