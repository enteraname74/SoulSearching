package com.github.enteraname74.localdb

import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.enteraname74.domain.util.AppEnvironment
import java.io.File

actual class RoomPlatformBuilder {
    actual fun builder(): RoomDatabase.Builder<AppDatabase> {
        val userHome = System.getProperty("user.home") ?: ""
        val userFolder = File(userHome)
        val appFolder = File(userFolder, APP_FOLDER)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }

        val dbFile = File(appFolder, "SoulSearching.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )
    }

    private val SUFFIX = if (AppEnvironment.IS_IN_DEVELOPMENT) {
        "_dev"
    } else {
        ""
    }
    private val APP_FOLDER: String = ".soul_searching$SUFFIX"
}