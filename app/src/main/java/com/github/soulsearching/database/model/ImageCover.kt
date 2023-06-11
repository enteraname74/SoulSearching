package com.github.soulsearching.database.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class ImageCover(
    @PrimaryKey(autoGenerate = true) val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : Bitmap? = null
)