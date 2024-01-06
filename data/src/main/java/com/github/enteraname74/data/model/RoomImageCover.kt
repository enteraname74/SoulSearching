package com.github.enteraname74.data.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.ImageCover
import java.io.ByteArrayOutputStream
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

/**
 * Converts a RoomImageCover to an ImageCover.
 */
internal fun RoomImageCover.toImageCover(): ImageCover {
    val bytes = if (cover == null) {
        null
    } else {
        val outputStream = ByteArrayOutputStream()
        cover.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.toByteArray()
    }

    return ImageCover(
        id = id,
        coverId = coverId,
        cover = bytes
    )
}

/**
 * Converts an ImageCover to a RoomImageCover.
 */
internal fun ImageCover.toRoomImageCover(): RoomImageCover {
    val bitmap = if (cover == null) {
        null
    } else {
        BitmapFactory.decodeByteArray(cover, 0, cover!!.size)
    }

    return RoomImageCover(
        id = id,
        coverId = coverId,
        cover = bitmap
    )
}