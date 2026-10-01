package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.Bitmap

actual fun Bitmap.toImageBitmap(): ImageBitmap = asImageBitmap()
