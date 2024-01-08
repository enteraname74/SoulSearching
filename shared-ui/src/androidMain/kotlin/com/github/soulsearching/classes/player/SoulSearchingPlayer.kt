package com.github.soulsearching.classes.player

import android.app.Notification
import com.github.enteraname74.model.Music

/**
 * Represent the player used by the service.
 */
interface SoulSearchingPlayer {

    /**
     * Set a song to the player.
     */
    fun setMusic(music: Music)

    /**
     * Load the current music without playing it.
     */
    fun onlyLoadMusic()

    /**
     * Launch the loaded music of the player.
     */
    fun launchMusic()

    /**
     * Play or pause the current music.
     */
    fun togglePlayPause()

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int)

    /**
     * Play the next song in the queue.
     */
    fun next()

    /**
     * Play the previous song in the queue.
     */
    fun previous()

    /**
     * Check if the player is playing.
     */
    fun isPlaying(): Boolean

    /**
     * Dismiss the player.
     */
    fun dismiss()

    /**
     * Retrieve the current position in the current played song.
     */
    fun getMusicPosition(): Int

    /**
     * Update the notification of the player.
     */
    fun updateNotification()

    /**
     * Retrieve the instance of the notification of the player.
     */
    fun getNotification(): Notification
}