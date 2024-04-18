package com.github.enteraname74.domain.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap

/**
 * Converts an ImageBitmap to a ByteArray representation of it.
 */
actual fun ImageBitmap?.toBytes(): ByteArray {
    if (this == null) return byteArrayOf()

    return this.asSkiaBitmap().readPixels() ?: byteArrayOf()
}