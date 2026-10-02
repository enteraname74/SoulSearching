package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.ui.graphics.ImageBitmap
import coil3.Bitmap

expect fun Bitmap.toImageBitmap(): ImageBitmap
