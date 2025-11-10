package com.github.enteraname74.soulsearching.model.utils

import android.content.Context
import android.provider.MediaStore

/**
 * All kind of different methods for android.
 */
object AndroidUtils {

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