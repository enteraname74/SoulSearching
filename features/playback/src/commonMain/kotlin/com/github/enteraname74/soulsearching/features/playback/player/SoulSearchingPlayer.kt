package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow

/**
 * Represent the player used by the service.
 */
interface SoulSearchingPlayer {
    val state: Flow<Boolean>
    var listener: Listener?

    /**
     * Initialize the player.
     */
    fun init()

    fun registerListener(listener: Listener)

    /**
     * Set a song to the player.
     */
    suspend fun setMusic(music: Music)

    /**
     * Load the current music without playing it.
     * @param seekTo the initial position to go when loading a song.
     */
    fun onlyLoadMusic(seekTo: Int)

    /**
     * Launch the loaded music of the player.
     */
    fun launchMusic()

    /**
     * Play or pause the current music.
     */
    fun togglePlayPause() {
        try {
            when (isPlaying()) {
                true -> pause()
                false -> play()
                null -> {
                    // no-op
                }
            }
        } catch (_: Exception) {}
    }

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
    fun isPlaying(): Boolean?

    /**
     * Dismiss the player.
     */
    fun dismiss()

    /**
     * Retrieve the current position in the current played song in milliseconds.
     */
    fun getMusicPosition(): Int

    /**
     * Retrieve the current music duration.
     * Returns 0 if no song is being played.
     */
    fun getMusicDuration(): Int

    /**
     * Set the volume of the player between 0.1 and 1.0.
     */
    fun setPlayerVolume(volume: Float)

    interface Listener {
        /**
         * Called when the current played song is completed
         */
        suspend fun onCompletion()
        suspend fun onError()
    }
}