package com.github.enteraname74.soulsearching.features.filemanager.util

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

actual class MusicMetadataHelper {
    actual fun getMetadataFromPaths(
        musicPaths: List<String>,
        fields: List<MetadataField>
    ): Map<String, Map<MetadataField, String>> = buildMap {
        for (path in musicPaths) {
            val file = File(path)
            if (!file.exists()) continue

            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag: Tag = audioFile.tag

            put(
                key = path,
                value = buildMap {
                    fields.forEach { field ->
                        tag.getField(field)?.let { value ->
                            put(
                                key = field,
                                value = value,
                            )
                        }
                    }
                }
            )
        }
    }

    private fun MetadataField.toTag(): FieldKey =
        when(this) {
            MetadataField.Track -> FieldKey.TRACK
            MetadataField.AlbumArtist -> FieldKey.ALBUM_ARTIST
        }

    private fun Tag.getField(metadataField: MetadataField): String? =
        runCatching {
            when(metadataField) {
                MetadataField.Track -> getFirst(metadataField.toTag())?.takeIf { it.isNotBlank() }
                MetadataField.AlbumArtist -> getFirst(metadataField.toTag())?.toIntOrNull()?.toString()
            }
        }.getOrNull()
}