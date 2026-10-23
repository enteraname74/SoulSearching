package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL

object Migration22To23 : Migration(22, 23) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.executeSQL(
            "ALTER TABLE RoomSoulSearchingSettings DROP COLUMN release_latestReleaseAssetName"
        )
        connection.executeSQL(
            "ALTER TABLE RoomSoulSearchingSettings DROP COLUMN release_latestReleaseAssetDownloadUrl"
        )
        connection.executeSQL(
            "ALTER TABLE RoomSoulSearchingSettings DROP COLUMN release_latestReleaseAssetSize"
        )
        connection.executeSQL(
            "ALTER TABLE RoomSoulSearchingSettings DROP COLUMN release_latestReleaseAssetSha256"
        )
    }
}
