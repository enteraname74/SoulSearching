package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.model.SyncedLyric
import kotlinx.serialization.Serializable

@Serializable
data class RemoteLyrics(
    val plainLyrics: String?,
    val syncedLyrics: String?,
) {
    fun toMusicLyrics(): MusicLyrics? =
        plainLyrics?.let {
            MusicLyrics(
                plainLyrics = it.split("\n"),
                syncedLyrics = formatSyncedLyrics()
            )
        }

    private fun formatSyncedLyrics(): List<SyncedLyric>? =
        syncedLyrics?.let { lyrics ->
            val timestampRegexContent = """\[\d\d:\d\d.\d\d]"""
            val timestampRegex = Regex(timestampRegexContent)
            val lyricsRegex = Regex("""(?!$timestampRegexContent) .*""")

            val lines: List<String> = lyrics.split("\n")
            buildList {
                for (line in lines) {
                    val timestamp: String = timestampRegex.find(line)?.groupValues?.getOrNull(0) ?: continue
                    val timestampMs: Long = StringTimestampConverter(timestamp).convert() ?: continue

                    val text: String = lyricsRegex
                        .find(line)
                        ?.groupValues
                        ?.getOrNull(0)
                        ?.trim()
                        ?.replace("\n", "")?: continue

                    add(
                        SyncedLyric(
                            line = text,
                            timestampMs = timestampMs,
                        )
                    )
                }
            }
        }
}