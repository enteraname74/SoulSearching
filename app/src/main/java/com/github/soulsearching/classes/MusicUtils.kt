package com.github.soulsearching.classes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.github.soulsearching.R
import com.github.soulsearching.database.model.Music
import java.io.File
import java.io.IOException

object MusicUtils {
    fun fetchMusics(
        context: Context,
        updateProgress: (Float) -> Unit,
        addingMusicAction: (Music, Bitmap?) -> Unit,
        finishAction: () -> Unit
    ) {
        Log.d("FETCHING MUSIC", "START")
        val mediaMetadataRetriever = MediaMetadataRetriever()

        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Albums.ALBUM_ID
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
        Log.d("FETCHING MUSIC", "CURSOR : ${cursor?.count}")
        when (cursor?.count) {
            null -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.cannot_retrieve_musics),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                var count = 0
                while (cursor.moveToNext()) {
                    val albumCover: Bitmap? = try {
                        mediaMetadataRetriever.setDataSource(cursor.getString(4))
                        try {
                            val byteArray = mediaMetadataRetriever.embeddedPicture
                            if (byteArray == null) {
                                null
                            } else {
                                val options = BitmapFactory.Options()
                                options.inSampleSize = 2
                                val tempBitmap = BitmapFactory.decodeByteArray(
                                    byteArray,
                                    0,
                                    byteArray.size,
                                    options
                                )
                                ThumbnailUtils.extractThumbnail(
                                    tempBitmap,
                                    Utils.BITMAP_SIZE,
                                    Utils.BITMAP_SIZE
                                )
                            }
                        } catch (error: IOException) {
                            null
                        }
                    } catch (error: java.lang.RuntimeException) {
                        null
                    }

                    val music = Music(
                        name = cursor.getString(0).trim(),
                        album = cursor.getString(2).trim(),
                        artist = cursor.getString(1).trim(),
                        duration = cursor.getLong(3),
                        path = cursor.getString(4),
                        folder = File(cursor.getString(4)).parent ?: ""
                    )
                    addingMusicAction(music, albumCover)
                    albumCover?.recycle()
                    count++
                    updateProgress((count * 1F) / cursor.count)
                }
                cursor.close()
                finishAction()
            }
        }
    }

}