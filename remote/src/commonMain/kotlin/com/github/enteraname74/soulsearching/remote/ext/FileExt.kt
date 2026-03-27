package com.github.enteraname74.soulsearching.remote.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

suspend fun File.contentType(): String =
    withContext(Dispatchers.IO) {
        Files.probeContentType(this@contentType.toPath())
    } ?: "application/octet-stream"