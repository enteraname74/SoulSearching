package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Album(
    @PrimaryKey
    val albumId : UUID = UUID.randomUUID(),
    var name : String = "",
    var albumCover : Bitmap? = null,
    var artist : String = ""
)