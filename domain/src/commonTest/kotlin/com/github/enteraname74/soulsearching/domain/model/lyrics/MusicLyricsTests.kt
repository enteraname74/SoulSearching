package com.github.enteraname74.soulsearching.domain.model.lyrics

import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import kotlin.test.Test
import kotlin.test.assertEquals

class MusicLyricsTests {
    @Test
    fun givenSyncedLyricsString_whenBuildingSyncedLyrics_thenShouldBeCorrect() {
        val syncedLyricsString = "[00:25.02] First line\n[00:27.25] Second line\n[00:30.85]Third line"
        val expectedSyncedLyrics = listOf(
            MusicLyrics.SyncedLyric(
                line = "First line",
                timestampMs = 25002L,
            ),
            MusicLyrics.SyncedLyric(
                line = "Second line",
                timestampMs = 27025L,
            ),
            MusicLyrics.SyncedLyric(
                line = "Third line",
                timestampMs = 30085L,
            ),
        )

        val actualSyncedLyrics = MusicLyrics.buildSyncedLyrics(syncedLyricsString)

        assertEquals(
            expected = expectedSyncedLyrics,
            actual = actualSyncedLyrics,
        )
    }

    @Test
    fun givenSyncedLyricsString_whenBuildingCleanPlainLyrics_thenShouldRemoveTimestamps() {
        val syncedLyricsString = "[00:25.02] First line\n[00:27.25] Second line\n[00:30.85]Third line"
        val expectedPlainLyrics = listOf(
            "First line",
            "Second line",
            "Third line",
        )
        val actualPlainLyrics = MusicLyrics.cleanPlainLyrics(syncedLyricsString)

        assertEquals(
            expected = expectedPlainLyrics,
            actual = actualPlainLyrics,
        )
    }

    @Test
    fun givenPlainLyricsString_whenBuildingCleanLyrics_thenNothingShouldChange() {
        val syncedLyricsString = "First line\n Second line\nThird line"
        val expectedPlainLyrics = listOf(
            "First line",
            "Second line",
            "Third line",
        )
        val actualPlainLyrics = MusicLyrics.cleanPlainLyrics(syncedLyricsString)

        assertEquals(
            expected = expectedPlainLyrics,
            actual = actualPlainLyrics,
        )
    }
}