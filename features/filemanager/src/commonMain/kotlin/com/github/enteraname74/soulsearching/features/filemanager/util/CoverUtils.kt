package com.github.enteraname74.soulsearching.features.filemanager.util

import org.jaudiotagger.audio.AudioFileIO
import java.io.File

object CoverUtils {
    fun getCoverOfMusicFile(musicPath: String): ByteArray? =
        try {
            val audioFile = AudioFileIO.read(File(musicPath))
            audioFile?.tag?.firstArtwork?.binaryData
        } catch (_: Exception) {
            null
        }
}