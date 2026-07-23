package com.github.enteraname74.localdb

import androidx.room3.Room
import androidx.room3.RoomDatabase

actual class RoomPlatformBuilder {
    actual fun builder(): RoomDatabase.Builder<AppDatabase> =
        Room.databaseBuilder<AppDatabase>(
            name = "SoulSearching.db",
        )
}
