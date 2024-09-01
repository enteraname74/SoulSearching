package com.github.enteraname74.domain.ext

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val options = BitmapFactory.Options()
    options.inSampleSize = 2
    val tempBitmap = BitmapFactory.decodeByteArray(
        this,
        0,
        this.size,
        options
    )
    return ThumbnailUtils.extractThumbnail(
        tempBitmap,
        BITMAP_SIZE,
        BITMAP_SIZE
    ).asImageBitmap()
}

private const val BITMAP_SIZE = 300