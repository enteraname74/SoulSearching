package com.github.soulsearching.classes.utils

import com.github.soulsearching.viewModels.PlayerViewModel

/**
 * Object containing the instance of the player view model.
 * It is static because we need to access this view model in multiple parts of the applications
 * like the service for example.
 */
object PlayerUtils {
    lateinit var playerViewModel: PlayerViewModel

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
