package com.github.enteraname74.soulsearching.domain.repository

import com.github.enteraname74.soulsearching.domain.model.lyrics.LyricsFetchState
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyricsRequest
import kotlinx.coroutines.flow.Flow

/**
 * Repository for songs lyrics
 */
interface LyricsRepository {
    fun observeLyrics(
        songProgressionFlow: Flow<Int>,
    ): Flow<LyricsFetchState>
}
