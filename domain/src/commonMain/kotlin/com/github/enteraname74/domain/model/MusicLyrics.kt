package com.github.enteraname74.domain.model

data class MusicLyrics(
    val plainLyrics: List<String>,
    val syncedLyrics: List<SyncedLyric>?,
)

data class SyncedLyric(
    val line: String,
    val timestampMs: Long,
)
