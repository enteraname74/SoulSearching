package com.github.enteraname74.soulsearching.util

import io.github.vinceglb.filekit.PlatformFile

expect object FilePlatformUtils {
    fun getPath(file: PlatformFile): String
}