package com.github.enteraname74.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
internal data class RoomImageCover(
    @PrimaryKey(autoGenerate = true) val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : Bitmap? = null
)