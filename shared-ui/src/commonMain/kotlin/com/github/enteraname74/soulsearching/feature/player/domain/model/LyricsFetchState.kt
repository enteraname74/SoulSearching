package com.github.enteraname74.soulsearching.feature.player.domain.model

/**
 * State for fetching lyrics.
 */
sealed interface LyricsFetchState {
    data object NoLyricsFound: LyricsFetchState

    data object FetchingLyrics: LyricsFetchState

    data class FoundLyrics(val lyrics: String): LyricsFetchState
}
