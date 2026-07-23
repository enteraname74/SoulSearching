package com.github.enteraname74.localdb

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

internal actual fun localDatabaseDriver(): SQLiteDriver = BundledSQLiteDriver()
