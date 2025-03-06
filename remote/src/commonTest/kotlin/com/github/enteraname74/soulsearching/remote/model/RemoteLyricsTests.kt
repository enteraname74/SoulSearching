package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.model.SyncedLyric
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteLyricsTests {
    @Test
    fun givenRemoteLyricsWithSyncedLyrics_whenConvertingToMusicLyrics_thenShouldHaveCorrectFormattedSyncedLyrics() {
        val remoteLyrics = RemoteLyrics(
            plainLyrics = "First line\nSecond line\nThird line",
            syncedLyrics = "[00:25.02] First line\n[00:27.25] Second line\n[00:30.85] Third line"
        )

        val actualMusicLyrics: MusicLyrics? = remoteLyrics.toMusicLyrics()
        val expectedMusicLyrics = MusicLyrics(
            plainLyrics = "First line\nSecond line\nThird line",
            syncedLyrics = listOf(
                SyncedLyric(
                    line = "First line",
                    timestampMs = 25002L,
                ),
                SyncedLyric(
                    line = "Second line",
                    timestampMs = 27025L,
                ),
                SyncedLyric(
                    line = "Third line",
                    timestampMs = 30085L,
                ),
            )
        )

        assertEquals(expectedMusicLyrics, actualMusicLyrics)
    }
}