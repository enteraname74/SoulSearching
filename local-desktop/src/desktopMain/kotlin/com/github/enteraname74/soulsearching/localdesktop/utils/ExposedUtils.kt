package com.github.enteraname74.soulsearching.localdesktop.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.util.*

/**
 * Utils related to the use of Exposed.
 */
object ExposedUtils {

    /**
     * Converts a string representation of an ImageBitmap to an ImageBitmap.
     */
    fun stringToImageBitmap(stringBitmap: String?): ImageBitmap? {
        if (stringBitmap == "" || stringBitmap == null) return null

        val imageBytes = Base64.getDecoder().decode(stringBitmap)
        return Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
    }

    /**
     * Converts an ImageBitmap to a string representation of it.
     */
    fun imageBitmapToString(imageBitmap: ImageBitmap?): String? {
        if (imageBitmap == null) return null
        imageBitmap.asSkiaBitmap().readPixels()?.let {
            return Base64.getEncoder().encodeToString(it)
        }

        return null
    }
}