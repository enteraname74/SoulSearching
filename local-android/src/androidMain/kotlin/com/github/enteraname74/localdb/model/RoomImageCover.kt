package com.github.enteraname74.localdb.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room representation of an ImageCover.
 */
@Entity
internal data class RoomImageCover(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val coverId: UUID = UUID.randomUUID(),
    val cover: Bitmap? = null
)