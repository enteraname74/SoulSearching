package com.github.enteraname74.soulsearching.features.filemanager.cloud

import com.github.enteraname74.domain.util.AppEnvironment
import java.io.File

class CloudCacheManagerDesktopImpl: CloudCacheManager {
    override val folder: File
        get(){
            val userHome = System.getProperty("user.home") ?: ""
            val userFolder = File(userHome)
            val coverFolder = File(userFolder, CLOUD_FOLDER)
            if (!coverFolder.exists()) {
                coverFolder.mkdirs()
            }

            return coverFolder
        }

    companion object {
        private val SUFFIX = if (AppEnvironment.IS_IN_DEVELOPMENT) {
            "_dev"
        } else {
            ""
        }
        private val CLOUD_FOLDER: String = ".soul_searching$SUFFIX/cloud"
    }
}