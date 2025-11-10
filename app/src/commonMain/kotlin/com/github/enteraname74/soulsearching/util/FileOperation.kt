package com.github.enteraname74.soulsearching.util

expect class FileOperation {
    fun buildSafePath(parent: String, child: String): String?
}