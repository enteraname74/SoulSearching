package com.github.enteraname74.localdb

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

actual class RoomPlatformBuilder(private val context: Context) {
    actual fun builder(): RoomDatabase.Builder<AppDatabase> =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "SoulSearching.db"
        )
}
