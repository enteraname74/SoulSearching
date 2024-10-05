package com.github.enteraname74.domain.util

import android.content.Context
import java.io.File
import java.util.UUID

class CoverFileManagerAndroidImpl(
    private val context: Context,
): CoverFileManager {

    override fun getCoverFolder(): File {
        val filesDir: File = context.filesDir
        val folder: File = File(filesDir, COVER_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        return folder
    }

    companion object {
        private const val COVER_FOLDER = "covers"
    }
}