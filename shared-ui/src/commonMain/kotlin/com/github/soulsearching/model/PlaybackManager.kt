package com.github.soulsearching.model

import com.github.enteraname74.domain.model.Music

/**
 * Used to manage the music playback of the application.
 */
interface PlaybackManager {

    /**
     * Initialize the player and the view from a saved music list (from the last app session).
     */
    fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>)

    /**
     * Initialize the MusicPlayerManager.
     * It must be used when needing to start the playback.
     * It is not meant to be used when the class itself is initialized.
     */
    fun initializeMusicPlayerManager(isFromSavedList: Boolean)

    /**
     * Set and play the current music from the player view model.
     */
    fun setAndPlayCurrentMusic()

    /**
     * Load the current music of the player view model.
     * The music will not be played.
     */
    fun onlyLoadMusic()

    /**
     * Check if the player is currently playing a song.
     * If the player is not defined yet, it will return false.
     */
    fun isPlayerPlaying(): Boolean

    /**
     * Toggle the play pause action of the player if it's defined.
     */
    fun togglePlayPause()

    /**
     * Play the next song in queue.
     */
    fun playNext()

    /**
     * Play the previous song in queue.
     */
    fun playPrevious()

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int)

    /**
     * Retrieve the current played music duration.
     * Return 0 if no music is playing.
     */
    fun getMusicDuration(): Int

    /**
     * Retrieve the current position in the current played music.
     * Return 0 if the player is not defined.
     */
    fun getCurrentMusicPosition(): Int

    /**
     * Stop the playback.
     * If will stop the service, dismiss the player and reset the player view model data.
     */
    fun stopMusic()

    /**
     * Force the update of the notification of the device displaying music information.
     */
    fun updateNotification()
}