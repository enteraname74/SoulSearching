package com.github.enteraname74.localdb.migration

import android.annotation.SuppressLint
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class Migration18To19(
    private val musicMetadataHelper: MusicMetadataHelper,
): Migration(18, 19) {
    @SuppressLint("Range")
    @OptIn(ExperimentalUuidApi::class)
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumPosition INTEGER")
        val cursor = db.query("SELECT musicId, path FROM RoomMusic")

        while(cursor.moveToNext()) {
            val coverIdBlob: ByteArray = cursor.getBlob(cursor.getColumnIndex("musicId"))

            val path = cursor.getString(cursor.getColumnIndex("path"))

            val albumPosition: Int? = musicMetadataHelper.getMusicAlbumPosition(musicPath = path)
            val sqlValue: String = albumPosition?.toString() ?: "NULL"

            db.execSQL("UPDATE RoomMusic SET albumPosition = $sqlValue WHERE musicId = '$coverIdBlob'")
        }
    }
}