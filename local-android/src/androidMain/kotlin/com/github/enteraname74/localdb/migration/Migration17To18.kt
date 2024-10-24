package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration17To18: Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE RoomMusic DROP COLUMN initialCoverPath")
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating from 17 to 18")
        }
    }
}