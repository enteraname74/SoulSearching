package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.soulsearching.localdesktop.AppDatabase

actual object LocalDatabaseInitializer {
    actual fun init() {
        AppDatabase.connectToDatabase()
    }
}