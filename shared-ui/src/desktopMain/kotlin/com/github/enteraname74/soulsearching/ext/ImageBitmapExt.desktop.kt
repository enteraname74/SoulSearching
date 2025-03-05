package com.github.enteraname74.soulsearching.ext

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toByteArray(): ByteArray {
    val skiaImage = Image.makeFromBitmap(this.asSkiaBitmap())
    val byteArrayOutputStream = ByteArrayOutputStream()
    byteArrayOutputStream.write(skiaImage.encodeToData()?.bytes ?: byteArrayOf())
    return byteArrayOutputStream.toByteArray()
}