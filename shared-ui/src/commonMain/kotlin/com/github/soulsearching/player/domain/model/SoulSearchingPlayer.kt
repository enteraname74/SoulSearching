package com.github.soulsearching.player.domain.model

import com.github.enteraname74.domain.model.Music

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
     * Play the loaded music.
     */
    fun play()

    /**
     * Pause the current music.
     */
    fun pause()

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int)

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
     * Retrieve the current music duration.
     * Returns 0 if no song is being played.
     */
    fun getMusicDuration(): Int
}