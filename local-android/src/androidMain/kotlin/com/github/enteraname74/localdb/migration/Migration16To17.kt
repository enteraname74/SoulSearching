package com.github.enteraname74.localdb.migration

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.util.CoverFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

internal class Migration16To17(
    private val context: Context,
    private val coverFileManager: CoverFileManager
): Migration(16, 17) {

    @OptIn(ExperimentalUuidApi::class)
    @SuppressLint("Range")
    private fun imageCoverMigration(db: SupportSQLiteDatabase) {
        // Step 2: Migrate data from ImageCover table
        val cursor = db.query("SELECT coverId, cover FROM RoomImageCover")

        // Step 3: Loop through the ImageCover table and save bitmaps to internal storage
        while (cursor.moveToNext()) {
            val coverIdBlob: ByteArray = cursor.getBlob(cursor.getColumnIndex("coverId"))
            val coverId = Uuid.fromByteArray(coverIdBlob).toJavaUuid()

            val coverAsString = cursor.getString(cursor.getColumnIndex("cover"))

            if (!coverAsString.isNullOrEmpty()) {
                val imageBytes = Base64.decode(coverAsString, 0)
                CoroutineScope(Dispatchers.IO).launch {
                    coverFileManager.saveCover(
                        id = coverId,
                        data = imageBytes,
                    )
                }
            }
        }
        cursor.close()

        // Step 4: Drop the ImageCover table
        db.execSQL("DROP TABLE IF EXISTS ImageCover")
    }

    @OptIn(ExperimentalUuidApi::class)
    @SuppressLint("Range")
    private fun musicInitialCoverPathMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN initialCoverPath TEXT")

        // Step 2: Query the Music table to retrieve the path of each song
        val musicCursor = db.query("SELECT musicId, path FROM RoomMusic")

        // Step 3: Loop through the Music table and update 'initialCoverPath'
        while (musicCursor.moveToNext()) {
            val blobId = musicCursor.getBlob(musicCursor.getColumnIndex("musicId"))
            val id: UUID = Uuid.fromByteArray(blobId).toJavaUuid()
            val musicFilePath = musicCursor.getString(musicCursor.getColumnIndex("path"))

            // Retrieve the album cover path using the music file path
            val initialCoverPath = getInitialCoverPathOfSong(musicFilePath)

            // Update the 'initialCoverPath' column for each row in the Music table
            if (initialCoverPath != null) {
                db.execSQL(
                    "UPDATE RoomMusic SET initialCoverPath = ? WHERE musicId = ?",
                    arrayOf(initialCoverPath, id)
                )
            }
        }
        musicCursor.close()
    }

    @SuppressLint("Range")
    override fun migrate(db: SupportSQLiteDatabase) {
        println("DATABASE -- Start migrating from 16 to 17")
        imageCoverMigration(db = db)
        musicInitialCoverPathMigration(db = db)
    }

    private fun getInitialCoverPathOfSong(musicFilePath: String): String? =
        getAlbumIdFromMusicPath(musicFilePath)?.let(::getMusicFileCoverPath)

    private fun getMusicFileCoverPath(albumId: Long): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use the content URI
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId).toString()
        } else {
            // For older versions, use the direct file path via ALBUM_ART
            getMusicFileCoverPathForOldDevices(albumId)
        }
    }

    @Suppress("DEPRECATION")
    private fun getMusicFileCoverPathForOldDevices(albumId: Long): String? {
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

    private fun getAlbumIdFromMusicPath(musicFilePath: String): Long? {
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID // Column to fetch the album ID
        )

        // Build the query with the file path as the selection
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(musicFilePath)

        // Perform the query on MediaStore.Audio.Media
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
}