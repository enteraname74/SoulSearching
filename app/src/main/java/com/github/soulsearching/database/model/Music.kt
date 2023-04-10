package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Music(
    @PrimaryKey
    val musicId: UUID,
    var name: String,
    val album: String,
    val artist: String,
    var albumCover: Bitmap? = null,
    var duration: Long,
    var path: String
)