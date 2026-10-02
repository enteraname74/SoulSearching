package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import coil3.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun Bitmap.toImageBitmap(): ImageBitmap {
    val data = Image.makeFromBitmap(this).encodeToData(EncodedImageFormat.PNG, 100)
        ?: error("Unable to encode bitmap.")
    return data.bytes.decodeToImageBitmap()
}
