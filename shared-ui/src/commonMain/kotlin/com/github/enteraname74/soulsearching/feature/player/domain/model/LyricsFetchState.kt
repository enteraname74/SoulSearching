package com.github.enteraname74.soulsearching.feature.player.domain.model

import com.github.enteraname74.domain.model.MusicLyrics

/**
 * State for fetching lyrics.
 */
sealed interface LyricsFetchState {
    data object NoLyricsFound: LyricsFetchState

    data object FetchingLyrics: LyricsFetchState

    data class FoundLyrics(
        val lyrics: MusicLyrics,
        val highlightedLyricsLine: Int?,
    ): LyricsFetchState
}
