package com.github.enteraname74.soulsearching.features.filemanager.cloud

import android.content.Context
import java.io.File

class CloudCacheManagerAndroidImpl(
    private val context: Context,
): CloudCacheManager {
    override val folder: File
        get() {
            val filesDir: File = context.filesDir
            val folder = File(filesDir, CLOUD_FOLDER)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return folder
        }

    companion object {
        private const val CLOUD_FOLDER = "cloud"
    }
}