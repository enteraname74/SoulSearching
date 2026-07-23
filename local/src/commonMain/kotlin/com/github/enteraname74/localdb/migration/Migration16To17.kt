package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.room3.util.getColumnIndex
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL
import androidx.sqlite.async.prepare
import androidx.sqlite.async.step
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class Migration16To17(
    private val coverFileManager: CoverFileManager
) : Migration(16, 17) {

    @OptIn(ExperimentalUuidApi::class, ExperimentalEncodingApi::class)
    private suspend fun imageCoverMigration(connection: SQLiteConnection) {
        val cursor = connection.prepare("SELECT coverId, cover FROM RoomImageCover")


        cursor.use {
            while (cursor.step()) {
                val coverIdBlob: ByteArray = cursor.getBlob(getColumnIndex(cursor, "coverId"))
                val coverId = Uuid.fromByteArray(coverIdBlob)

                val coverAsString = if (!cursor.isNull(getColumnIndex(cursor, "cover"))) {
                    cursor.getText(getColumnIndex(cursor, "cover"))
                } else {
                    null
                }

                if (!coverAsString.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(coverAsString, 0)
                    coverFileManager.saveCover(
                        id = coverId,
                        data = imageBytes,
                    )
                }
            }
        }

        // Step 4: Drop the ImageCover table
        connection.executeSQL("DROP TABLE IF EXISTS ImageCover")
    }

    private suspend fun musicInitialCoverPathMigration(connection: SQLiteConnection) {
        connection.executeSQL("ALTER TABLE RoomMusic ADD COLUMN initialCoverPath TEXT")
    }

    override suspend fun migrate(connection: SQLiteConnection) {
        try {
            imageCoverMigration(connection = connection)
            musicInitialCoverPathMigration(connection = connection)
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating fro 16 to 17: ${e.message}")
        }
    }
}
