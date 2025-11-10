package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object Migration17To18: Migration(17, 18) {
    override fun migrate(connection: SQLiteConnection) {
        try {
            connection.execSQL("ALTER TABLE RoomMusic DROP COLUMN initialCoverPath")
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating from 17 to 18")
        }
    }
}