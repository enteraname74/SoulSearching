package com.github.enteraname74.soulsearching.features.filemanager.util

actual class MusicMetadataHelper {
    actual fun getMetadataFromPaths(
        musicPaths: List<String>,
        fields: List<MetadataField>,
    ): Map<String, Map<MetadataField, String>> = emptyMap()
}
