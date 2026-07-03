package com.github.enteraname74.localdb

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

internal actual fun localDatabaseDriver(): SQLiteDriver =
    WebWorkerSQLiteDriver(
        Worker(js("""new URL("sqlite-wasm-worker/worker.js", import.meta.url)"""))
    )
