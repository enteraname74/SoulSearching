package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class ImageCover(
    @PrimaryKey val coverId : UUID = UUID.randomUUID(),
    val cover : Bitmap? = null
)