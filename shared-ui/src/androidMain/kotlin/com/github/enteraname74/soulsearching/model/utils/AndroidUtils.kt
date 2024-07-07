package com.github.soulsearching.model.utils

import android.content.ContentResolver
import android.content.Context
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

    /**
     * Retrieve the media id from the media store of a music path.
     */
    fun musicPathToMediaId(context: Context, musicPath: String): Long {
        var id: Long = 0

        val uri = MediaStore.Files.getContentUri("external")
        val selection = MediaStore.Audio.Media.DATA
        val selectionArgs = arrayOf(musicPath)
        val projection = arrayOf(MediaStore.Audio.Media._ID)

        context.contentResolver.query(
            uri,
            projection,
            "$selection=?",
            selectionArgs,
            null
        )?.let { cursor ->
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                id = cursor.getString(idIndex).toLong()
            }
            cursor.close()
        }
        return id
    }
}