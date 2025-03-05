package com.github.enteraname74.soulsearching.localdesktop.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream

/**
 * Utils related to the use of Exposed.
 */
object ExposedUtils {

    fun imageBitmapToByteArray(imageBitmap: ImageBitmap?): ByteArray? {
        if (imageBitmap == null) return null

        val skiaImage = Image.makeFromBitmap(imageBitmap.asSkiaBitmap())
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(skiaImage.encodeToData()?.bytes ?: byteArrayOf())
        return byteArrayOutputStream.toByteArray()
    }


    fun byteArrayToImageBitmap(byteArray: ByteArray?): ImageBitmap? {
        if (byteArray == null) return null

        val skiaImage = Image.makeFromEncoded(byteArray)
        return skiaImage.toComposeImageBitmap()
    }
}