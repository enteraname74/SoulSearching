package com.github.enteraname74.soulsearching.util

import java.io.File

actual fun pathExists(path: String): Boolean = File(path).exists()
