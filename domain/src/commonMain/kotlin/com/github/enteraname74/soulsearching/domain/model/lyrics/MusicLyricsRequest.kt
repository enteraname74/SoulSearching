package com.github.enteraname74.soulsearching.domain.model.lyrics

data class MusicLyricsRequest(
    val localPath: String?,
    val musicName: String,
    val artistName: String,
    val albumName: String,
    val remoteFetchAvailable: Boolean,
)
