package com.github.enteraname74.domain.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

/**
 * Converts an ImageBitmap to a ByteArray representation of it.
 */
actual fun ImageBitmap?.toBytes(): ByteArray {
    if (this == null) return byteArrayOf()

    val outputStream = ByteArrayOutputStream()
    this.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 0, outputStream)
    return outputStream.toByteArray()
}