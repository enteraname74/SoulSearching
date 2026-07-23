package com.github.enteraname74.soulsearching.util

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.fromBookmarkData
import kotlinx.coroutines.runBlocking

actual object FilePlatformUtils {
    actual fun getPath(file: PlatformFile): String =
        runBlocking {
            PlatformFile.fromBookmarkData(file.bookmarkData()).absolutePath()
        }
}