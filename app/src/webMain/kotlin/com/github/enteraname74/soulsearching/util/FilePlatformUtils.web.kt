package com.github.enteraname74.soulsearching.util

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path

actual object FilePlatformUtils {
    actual fun getPath(file: PlatformFile): String = file.path
}