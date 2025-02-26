package com.github.enteraname74.soulsearching.remote.model

class StringTimestampConverter(
    private val stringTimestamp: String
) {
    fun convert(): Long? {
        val groups = preciseRegex.find(stringTimestamp)?.groupValues ?: return null

        if (groups.size <= 1) return null

        val timeValues = groups.subList(1, groups.size)
        var total = 0L

        val minutes: Long = timeValues.getOrNull(MINUTES_INDEX)?.toLongOrNull() ?: return null
        val seconds: Long = timeValues.getOrNull(SECONDS_INDEX)?.toLongOrNull() ?: return null
        val milliseconds: Long = timeValues.getOrNull(MILLISECONDS_INDEX)?.toLongOrNull() ?: return null

        total += milliseconds
        total += seconds * 1000
        total += minutes * 60 * 1000

        return total
    }

    companion object {
        private val preciseRegex = Regex("""\[(\d*):(\d\d).(\d\d)]""")

        private const val MINUTES_INDEX = 0
        private const val SECONDS_INDEX = 1
        private const val MILLISECONDS_INDEX = 2
    }
}