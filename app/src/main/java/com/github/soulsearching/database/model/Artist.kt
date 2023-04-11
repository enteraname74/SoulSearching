package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Artist(
    @PrimaryKey
    val artistId: UUID,
    var name: String,
    var artistCover: Bitmap? = null
)