package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Album(
    @PrimaryKey
    val albumId : UUID = UUID.randomUUID(),
    var albumName : String = "",
    var albumCover : Bitmap? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0
)