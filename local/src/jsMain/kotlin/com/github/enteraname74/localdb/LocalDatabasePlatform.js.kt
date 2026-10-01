package com.github.enteraname74.localdb

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.MODULE
import org.w3c.dom.Worker
import org.w3c.dom.WorkerOptions
import org.w3c.dom.WorkerType

internal actual fun localDatabaseDriver(): SQLiteDriver =
    WebWorkerSQLiteDriver(
        Worker(
            js("""new URL("sql-js-worker/worker.js", import.meta.url)"""),
            WorkerOptions(type = WorkerType.MODULE),
        )
    )
