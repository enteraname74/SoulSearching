package com.github.enteraname74.soulsearching.features.filemanager.cover

import com.github.enteraname74.domain.util.AppDirectories
import java.io.File
import kotlin.io.path.createDirectories

internal class CoverFileManagerDesktopImpl: JvmCoverFileManager() {
    override fun getCoverFolder(): File =
        AppDirectories.cache
            .resolve("covers")
            .createDirectories()
            .toFile()
}
