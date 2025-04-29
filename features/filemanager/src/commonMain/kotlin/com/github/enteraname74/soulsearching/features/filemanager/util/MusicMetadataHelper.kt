package com.github.enteraname74.soulsearching.features.filemanager.util

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

class MusicMetadataHelper {
    fun getFields(
        musicPath: String,
        fields: List<MetadataField>,
    ): Map<MetadataField, String> =
        runCatching {
            val audioFile: AudioFile = AudioFileIO.read(File(musicPath))
            val tag: Tag = audioFile.tag

            buildMap {
                fields.forEach { field ->
                    tag.getFirst(field.toFieldKey())?.takeIf { it.isNotBlank() }?.let {
                        put(field, it)
                    }
                }
            }
        }.getOrElse { emptyMap() }

    private fun MetadataField.toFieldKey(): FieldKey =
        when(this) {
            MetadataField.Track -> FieldKey.TRACK
            MetadataField.AlbumArtist -> FieldKey.ALBUM_ARTIST
        }
}

enum class MetadataField {
    Track,
    AlbumArtist;
}