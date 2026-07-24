package com.github.enteraname74.localdb

import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.github.enteraname74.domain.util.AppDirectories

actual class RoomPlatformBuilder {
    actual fun builder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = AppDirectories.data.resolve("SoulSearching.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.toString(),
        )
    }
}
