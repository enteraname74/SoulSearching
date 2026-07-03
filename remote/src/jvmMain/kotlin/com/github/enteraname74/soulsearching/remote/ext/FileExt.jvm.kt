package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

internal suspend fun File.contentType(workDispatcher: WorkDispatcher): String =
    withContext(workDispatcher.dispatcher) {
        Files.probeContentType(this@contentType.toPath())
    } ?: "application/octet-stream"
