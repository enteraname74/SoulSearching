package com.github.soulsearching.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class CurrentPlaylistItem(
    @PrimaryKey val itemId : UUID = UUID.randomUUID(),
    val musicId : UUID
)