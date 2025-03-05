package com.github.enteraname74.soulsearching.features.filemanager.cover

import android.content.Context
import java.io.File

internal class CoverFileManagerAndroidImpl(
    private val context: Context,
): CoverFileManager {

    override fun getCoverFolder(): File {
        val filesDir: File = context.filesDir
        val folder = File(filesDir, COVER_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        return folder
    }

    companion object {
        private const val COVER_FOLDER = "covers"
    }
}