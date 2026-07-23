package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL

object Migration17To18: Migration(17, 18) {
    override suspend fun migrate(connection: SQLiteConnection) {
        try {
            connection.executeSQL("ALTER TABLE RoomMusic DROP COLUMN initialCoverPath")
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating from 17 to 18")
        }
    }
}
