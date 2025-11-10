package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.room.util.getColumnIndex
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

internal class Migration16To17(
    private val coverFileManager: CoverFileManager
): Migration(16, 17) {

    @OptIn(ExperimentalUuidApi::class, ExperimentalEncodingApi::class)
    private fun imageCoverMigration(connection: SQLiteConnection) {
        val cursor = connection.prepare("SELECT coverId, cover FROM RoomImageCover")


        cursor.use {
            while (cursor.step()) {
                val coverIdBlob: ByteArray = cursor.getBlob(getColumnIndex(cursor, "coverId"))
                val coverId = Uuid.fromByteArray(coverIdBlob).toJavaUuid()

                val coverAsString = if (!cursor.isNull(getColumnIndex(cursor, "cover"))) {
                    cursor.getText(getColumnIndex(cursor, "cover"))
                } else {
                    null
                }

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
        }

        // Step 4: Drop the ImageCover table
        connection.execSQL("DROP TABLE IF EXISTS ImageCover")
    }

    private fun musicInitialCoverPathMigration(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE RoomMusic ADD COLUMN initialCoverPath TEXT")
    }

    override fun migrate(connection: SQLiteConnection) {
       try {
           println("DATABASE -- Start migrating from 16 to 17")
           imageCoverMigration(connection = connection)
           musicInitialCoverPathMigration(connection = connection)
       } catch (e: Exception) {
           println("DATABASE -- Error while migrating fro 16 to 17: ${e.message}")
       }
    }
}