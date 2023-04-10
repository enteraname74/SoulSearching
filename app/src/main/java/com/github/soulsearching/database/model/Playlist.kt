package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Playlist(
    @PrimaryKey
    val playlistId: UUID,
    var name: String,
    var playlistCover: Bitmap? = null,
    val isFavorite: Boolean = false
)