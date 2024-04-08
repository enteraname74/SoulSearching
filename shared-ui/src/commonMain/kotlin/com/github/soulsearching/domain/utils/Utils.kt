package com.github.soulsearching.domain.utils

/**
 * All kind of different methods.
 */
object Utils {
    /**
     * Convert a duration to a viewable duration.
     */
    fun convertDuration(duration: Int): String {
        val minutes: Float = duration.toFloat() / 1000 / 60
        val seconds: Float = duration.toFloat() / 1000 % 60

        val strMinutes: String = minutes.toString().split(".")[0]

        val strSeconds = if (seconds < 10.0) {
            "0" + seconds.toString().split(".")[0]
        } else {
            seconds.toString().split(".")[0]
        }

        return "$strMinutes:$strSeconds"
    }
}