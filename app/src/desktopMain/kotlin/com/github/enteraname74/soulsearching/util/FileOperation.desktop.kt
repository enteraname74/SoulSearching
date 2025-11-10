package com.github.enteraname74.soulsearching.util

actual class FileOperation {
    actual fun buildSafePath(parent: String, child: String): String? =
        "$parent/$child"
}