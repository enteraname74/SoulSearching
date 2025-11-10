package com.github.enteraname74.soulsearching.features.filemanager.util

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

actual class MusicMetadataHelper(
    private val context: Context,
) {
    actual fun getMetadataFromPaths(
        musicPaths: List<String>,
        fields: List<MetadataField>
    ): Map<String, Map<MetadataField, String>> {
        val projection: Array<String> = buildList {
            add(MediaStore.Audio.Media.DATA)

            addAll(fields.map { it.toProjectionElement() })
        }.toTypedArray()

        val pathChunks = musicPaths.chunked(CHUNK_SIZE)

        val data: HashMap<String, Map<MetadataField, String>> = HashMap()

        pathChunks.forEach { chunk ->
            val placeholders = chunk.joinToString(",") { "?" }
            val selection = "${MediaStore.Audio.Media.DATA} IN ($placeholders)"
            val selectionArgs = chunk.toTypedArray()

            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use { safeCursor ->
                val dataIndex: Int = safeCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

                while (safeCursor.moveToNext()) {
                    val filePath: String? = safeCursor.getString(dataIndex)

                    filePath?.let { path ->
                        val metadataMap: Map<MetadataField, String> = buildMap {
                            fields.forEach { field ->
                                val index: Int = safeCursor.getColumnIndex(field.toProjectionElement())
                                val value: String? =
                                    safeCursor.getField(
                                        index = index,
                                        field = field,
                                    )

                                value?.let {
                                    put(field, it)
                                }
                            }
                        }

                        data[path] = metadataMap
                    }
                }
            }
        }

        return data
    }

    private fun Cursor.getField(index: Int, field: MetadataField): String? =
        runCatching {
            when (field) {
                MetadataField.Track -> getString(index)?.toIntOrNull()?.toString()
                MetadataField.AlbumArtist -> getString(index)?.takeIf { it.isNotBlank() }
            }
        }.getOrNull()

    private fun MetadataField.toProjectionElement(): String =
        when (this) {
            MetadataField.Track -> MediaStore.Audio.Media.TRACK
            MetadataField.AlbumArtist -> MediaStore.Audio.Media.ALBUM_ARTIST
        }

    private companion object {
        const val CHUNK_SIZE = 500
    }
}