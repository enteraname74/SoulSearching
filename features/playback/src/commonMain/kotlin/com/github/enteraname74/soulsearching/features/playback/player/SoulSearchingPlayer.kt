package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import kotlin.time.Duration

/**
 * Represent the player used by the service.
 */
interface SoulSearchingPlayer {
    var listener: Listener?

    fun registerListener(listener: Listener) {
        this.listener = listener
    }

    /**
     * Set a song to the player.
     */
    suspend fun setMusic(music: Music)

    /**
     * Play the loaded music.
     */
    suspend fun play()

    /**
     * Pause the current music.
     */
    suspend fun pause()

    /**
     * Seek to a given position in the current played music.
     */
    suspend fun seekToPosition(millis: Int)

    /**
     * Check if the player is playing.
     */
    suspend fun isPlaying(): Boolean?

    suspend fun getState(): State

    /**
     * Dismiss the player.
     */
    suspend fun dismiss()

    /**
     * Retrieve the progress in the current played song.
     */
    suspend fun getProgress(): Duration

    /**
     * Retrieve the current music duration.
     * Returns a duration of 0 if no song is being played.
     */
    suspend fun getMusicDuration(): Duration

    /**
     * Set the volume of the player between 0.1 and 1.0.
     */
    suspend fun setPlayerVolume(volume: Float)

    enum class State {
        Playing,
        Paused,
        Idle,
    }

    interface Listener {
        /**
         * Called when the current played song is completed
         */
        suspend fun onCompletion()
        suspend fun onError()

        /**
         * Called when loosing playing capability (other app is playing an audio)
         */
        suspend fun onPause()

        /**
         * Called when receiving playing capability
         */
        suspend fun onPlay()
    }
}
