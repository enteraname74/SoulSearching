package com.github.soulsearching.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(primaryKeys = ["musicId", "playlistId"])
data class MusicPlaylist(
    val musicId: UUID = UUID.randomUUID(),
    val playlistId: UUID = UUID.randomUUID()
)