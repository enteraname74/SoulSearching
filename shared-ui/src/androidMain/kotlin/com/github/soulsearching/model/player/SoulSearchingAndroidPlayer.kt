package com.github.soulsearching.model.player

import android.app.Notification
import com.github.soulsearching.model.SoulSearchingPlayer

/**
 * Android specification of the SoulSearchingPlayer for managing notifications.
 */
abstract class SoulSearchingAndroidPlayer: SoulSearchingPlayer {
    /**
     * Update the notification of the player.
     */
    abstract fun updateNotification()

    /**
     * Retrieve the instance of the notification of the player.
     */
    abstract fun getNotification(): Notification
}