package com.github.enteraname74.soulsearching.features.filemanager.util

expect class MusicMetadataHelper {
    fun getMetadataFromPaths(
        musicPaths: List<String>,
        fields: List<MetadataField>,
    ): Map<String, Map<MetadataField, String>>
}

enum class MetadataField {
    Track,
    AlbumArtist;
}