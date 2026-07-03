package com.github.enteraname74.localdb

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun localDatabaseDriver(): SQLiteDriver = BundledSQLiteDriver()

internal actual val localDatabaseDispatcher: CoroutineDispatcher = Dispatchers.IO
