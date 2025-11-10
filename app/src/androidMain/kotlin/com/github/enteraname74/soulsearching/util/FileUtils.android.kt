package com.github.enteraname74.soulsearching.util

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

actual class FileOperation(private val context: Context) {
    actual fun buildSafePath(parent: String, child: String): String? = runCatching {
        val folderUri = parent.toUri()

        val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return null

        val parts = child.split("/").filter { it.isNotBlank() }
        val file = parts.fold(folder as DocumentFile?) { current, part ->
            current?.findFile(part)
        }
        file?.uri?.toString()
    }.getOrNull()
}