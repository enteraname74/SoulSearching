package com.github.enteraname74.soulsearching.features.filemanager.util

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore

internal object MusicCoverUtils {
    fun getMusicFileCoverPath(
        context: Context,
        albumId: Long
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId).toString()
        } else {
            getMusicFileCoverPathForOldDevices(context, albumId)
        }
    }

    fun getAlbumIdFromMusicPath(context: Context, musicFilePath: String): Long? {
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(musicFilePath)

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                // Retrieve the album ID from the cursor
                val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                return albumId
            }
        }

        return null
    }

    @Suppress("DEPRECATION")
    private fun getMusicFileCoverPathForOldDevices(
        context: Context,
        albumId: Long,
    ): String? {
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
        var albumArtPath: String? = null

        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                albumArtPath = it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
            }
        }
        return albumArtPath
    }
}