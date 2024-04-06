package com.github.soulsearching.model.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * All kind of different methods for android.
 */
object AndroidUtils {
    const val BITMAP_SIZE = 300

    /**
     * Retrieve a bitmap from a given Uri.
     */
    @Suppress("DEPRECATION")
    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): ImageBitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            contentResolver.loadThumbnail(
                uri,
                Size(BITMAP_SIZE, BITMAP_SIZE),
                null
            ).asImageBitmap()
        } else {
            Bitmap.createScaledBitmap(
                MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                ), BITMAP_SIZE, BITMAP_SIZE, false
            ).asImageBitmap()
        }
    }
}