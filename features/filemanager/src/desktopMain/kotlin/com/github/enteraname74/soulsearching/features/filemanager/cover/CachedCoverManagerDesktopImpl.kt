package com.github.enteraname74.soulsearching.features.filemanager.cover

import androidx.compose.ui.graphics.ImageBitmap
import org.jaudiotagger.audio.AudioFileIO
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.io.File

class CachedCoverManagerDesktopImpl: CachedCoverManager() {
    @OptIn(ExperimentalResourceApi::class)
    override fun fetchCoverOfMusicFile(musicPath: String): ImageBitmap? =
        try {
            val audioFile = AudioFileIO.read(File(musicPath))
            val data: ImageBitmap? = audioFile?.tag?.firstArtwork?.binaryData?.decodeToImageBitmap()
            data?.let { cachedImages[musicPath] = it }
            data
        } catch (_: Exception) {
            null
        }
}