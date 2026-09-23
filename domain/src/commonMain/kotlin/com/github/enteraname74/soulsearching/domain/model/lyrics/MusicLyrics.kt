package com.github.enteraname74.soulsearching.domain.model.lyrics

data class MusicLyrics(
    val plainLyrics: List<String>,
    val syncedLyrics: List<SyncedLyric>?,
    val provider: Provider,
) {
    companion object {
        private val timestampRegex = Regex("""^\[[0-9]+:[0-9]{2}\.[0-9]{1,2}\]""")

        fun buildPlainLyrics(stringLyrics: String): List<String> =
            stringLyrics
                .lineSequence()
                .map { line -> timestampRegex.replaceFirst(line, "").trim() }
                .filter(String::isNotEmpty)
                .toList()

        fun buildSyncedLyrics(stringLyrics: String): List<SyncedLyric>? {
            val lines: List<String> = stringLyrics.split("\n")
            return buildList {
                for (line in lines) {
                    val timestamp: String = timestampRegex.find(line)?.value ?: continue
                    val timestampMs: Long = StringTimestampConverter(timestamp).convert() ?: continue
                    val text: String = line.removePrefix(timestamp).trim()

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
