package com.github.enteraname74.soulsearching.features.musicmanager.domain

interface MusicMetadataHelper {
    suspend fun getMusicAlbumPosition(): Int?
}