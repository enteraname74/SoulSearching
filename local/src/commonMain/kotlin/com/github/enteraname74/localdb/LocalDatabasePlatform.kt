package com.github.enteraname74.localdb

import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher

internal expect fun localDatabaseDriver(): SQLiteDriver

internal expect val localDatabaseDispatcher: CoroutineDispatcher
