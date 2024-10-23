package com.github.enteraname74.soulsearching.features.filemanager.cover

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.AppEnvironment
import java.io.File

internal class CoverFileManagerDesktopImpl: CoverFileManager {
    override fun getCoverFolder(): File {
        val userHome = System.getProperty("user.home") ?: ""
        val userFolder = File(userHome)
        val coverFolder = File(userFolder, COVERS_FOLDER)
        if (!coverFolder.exists()) {
            coverFolder.mkdirs()
        }

        return coverFolder
    }

    override fun getCleanFileCoverForMusic(music: Music): Cover.FileCover =
        Cover.FileCover(
            initialCoverPath = music.path,
        )

    companion object {
        private val SUFFIX = if (AppEnvironment.IS_IN_DEVELOPMENT) {
            "_dev"
        } else {
            ""
        }
        private val COVERS_FOLDER: String = ".soul_searching$SUFFIX/covers"
    }
}