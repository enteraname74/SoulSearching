package com.github.enteraname74.soulsearching.domain.model.lyrics

/**
 * State for fetching lyrics.
 */
sealed interface LyricsFetchState {
    data object NoLyricsFound : LyricsFetchState

    data object FetchingLyrics : LyricsFetchState

    data object NoPermission : LyricsFetchState

    data class FoundLyrics(
        val lyrics: MusicLyrics,
        val highlightedLyricsLine: Int?,
    ) : LyricsFetchState
}