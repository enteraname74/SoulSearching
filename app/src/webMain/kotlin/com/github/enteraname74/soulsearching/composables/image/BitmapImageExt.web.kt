package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import coil3.Bitmap

actual fun Bitmap.toImageBitmap(): ImageBitmap =
    this.asComposeImageBitmap()
