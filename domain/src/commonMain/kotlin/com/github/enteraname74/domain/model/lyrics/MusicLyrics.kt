package com.github.enteraname74.domain.model.lyrics

data class MusicLyrics(
    val plainLyrics: List<String>,
    val syncedLyrics: List<SyncedLyric>?,
    val provider: Provider,
) {
    companion object {
        private const val TIMESTAMP_REGEX_CONTENT = """\[\d\d:\d\d.\d{1,2}]"""

        fun cleanPlainLyrics(stringLyrics: String): List<String> {
            val lines: List<String> = stringLyrics.split("\n")
            val lyricsRegex = Regex("""($TIMESTAMP_REGEX_CONTENT)(\s?.*)""")

            return buildList {
                for (line in lines) {
                    val text: String = lyricsRegex
                        .find(line)
                        ?.groupValues
                        ?.lastOrNull()
                        ?.trim()
                        ?.replace("\n", "") ?: continue

                    add(text)
                }
            }
        }

        fun buildSyncedLyrics(stringLyrics: String): List<SyncedLyric>? {
            val timestampRegex = Regex(TIMESTAMP_REGEX_CONTENT)
            val lyricsRegex = Regex("""($TIMESTAMP_REGEX_CONTENT)(\s?.*)""")

            val lines: List<String> = stringLyrics.split("\n")
            return buildList {
                for (line in lines) {
                    val timestamp: String = timestampRegex.find(line)?.groupValues?.getOrNull(0) ?: continue
                    val timestampMs: Long = StringTimestampConverter(timestamp).convert() ?: continue

                    val text: String = lyricsRegex
                        .find(line)
                        ?.groupValues
                        ?.lastOrNull()
                        ?.trim()
                        ?.replace("\n", "") ?: continue

                    add(
                        SyncedLyric(
                            line = text,
                            timestampMs = timestampMs,
                        )
                    )
                }
            }.takeIf { it.isNotEmpty() }
        }
    }

    data class SyncedLyric(
        val line: String,
        val timestampMs: Long,
    )

    enum class Provider {
        Remote,
        LocalFile
    }
}
