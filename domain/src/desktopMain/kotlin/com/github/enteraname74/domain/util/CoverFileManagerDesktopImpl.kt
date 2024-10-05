package com.github.enteraname74.domain.util

import java.io.File

class CoverFileManagerDesktopImpl: CoverFileManager {
    override fun getCoverFolder(): File {
        val userHome = System.getProperty("user.home") ?: ""
        val userFolder = File(userHome)
        val coverFolder = File(userFolder, COVERS_FOLDER)
        if (!coverFolder.exists()) {
            coverFolder.mkdirs()
        }

        return coverFolder
    }

    companion object {
        private val COVERS_FOLDER: String = ".soul_searching/covers"
    }
}