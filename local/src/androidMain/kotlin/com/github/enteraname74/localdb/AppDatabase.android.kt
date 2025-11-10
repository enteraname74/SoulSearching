package com.github.enteraname74.localdb

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class RoomPlatformBuilder(private val context: Context) {
    actual fun builder(): RoomDatabase.Builder<AppDatabase> =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "SoulSearching.db"
        )
}