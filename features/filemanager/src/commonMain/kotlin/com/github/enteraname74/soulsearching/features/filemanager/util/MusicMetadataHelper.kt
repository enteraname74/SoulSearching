package com.github.enteraname74.soulsearching.features.filemanager.util

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

class MusicMetadataHelper {
    fun getMusicAlbumPosition(musicPath: String): Int? =
        runCatching {
            val audioFile: AudioFile = AudioFileIO.read(File(musicPath))
            val tag: Tag = audioFile.tag
            tag.getFirst(FieldKey.TRACK)?.toIntOrNull()
        }.getOrNull()
}