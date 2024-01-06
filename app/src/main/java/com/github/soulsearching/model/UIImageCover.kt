package com.github.soulsearching.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.enteraname74.domain.model.ImageCover
import java.io.ByteArrayOutputStream
import java.util.UUID

/**
 * UI version of an ImageCover.
 * It uses a Bitmap instead of a ByteArray to represent the cover.
 */
data class UIImageCover(
    val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : Bitmap? = null
)

/**
 * Converts a UIImageCover to an ImageCover.
 */
internal fun UIImageCover.toImageCover(): ImageCover {
    val bytes = if (cover == null) {
        null
    } else {
        val outputStream = ByteArrayOutputStream()
        cover.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.toByteArray()
    }

    return ImageCover(
        id = id,
        coverId = coverId,
        cover = bytes
    )
}

/**
 * Converts an ImageCover to a UIImageCover.
 */
internal fun ImageCover.toUIImageCover(): UIImageCover {
    val bitmap = if (cover == null) {
        null
    } else {
        BitmapFactory.decodeByteArray(cover, 0, cover!!.size)
    }

    return UIImageCover(
        id = id,
        coverId = coverId,
        cover = bitmap
    )
}
