package com.github.enteraname74.localdb.migration

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    @SuppressLint("Range")
    private fun musicInitialCoverPathMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN initialCoverPath TEXT")
    }

    @SuppressLint("Range")
    override fun migrate(db: SupportSQLiteDatabase) {
        println("DATABASE -- Start migrating from 16 to 17")
        imageCoverMigration(db = db)
        musicInitialCoverPathMigration(db = db)
    }
}