package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Artist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var artistCover: Bitmap? = null
)