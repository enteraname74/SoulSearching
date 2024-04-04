package com.github.soulsearching.model

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.viewmodel.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Used to manage the music playback of the application.
 */
abstract class PlaybackManager {
    /**
     * The initial list, used by the playback manager.
     */
    protected var _initialList: ArrayList<Music> = ArrayList()
    val initialList: List<Music>
        get() = _initialList

    /**
     * The played list, used by the player and modified by the user (adding song...).
     */
    protected var _playedList: ArrayList<Music> = ArrayList()
    val playedList: List<Music>
        get() = _playedList

    /**
     * The currently played music.
     */
    protected var _currentMusic: Music? = null
    val currentMusic: Music?
        get() = _currentMusic

    /**
     * Retrieve the current music duration.
     * Returns 0 if no music is playing
     */
    val currentMusicDuration: Int
        get() = player.getMusicDuration()

    /**
     * Retrieve the current position in the current played music.
     * Returns 0 if nothing is being played.
     */
    val currentMusicPosition: Int
        get() = player.getMusicPosition()

    /**
     * The player used by the controller.
     */
    protected abstract val player: SoulSearchingPlayer

    /**
     * Check if the player is currently playing a song.
     * If the player is not defined yet, it will return false.
     */
    val isPlaying: Boolean
        get() = player.isPlaying()

    /**
     * Used for updating view elements.
     */
    var playerViewModel: PlayerViewModel? = null

    /**
     * Used to update frequently the current position in the duration
     * of the played music.
     */
    protected var durationJob: Job? = null

    /**
     * Set the lists used by the player (played and initial list).
     */
    fun setPlayerLists(musics: List<Music>) {
        _initialList = ArrayList(musics)
        _playedList = ArrayList(musics)
    }

    /**
     * Play or pause the player, depending on its current state.
     */
    fun togglePlayPause() {
        player.togglePlayPause()
        update()
    }

    /**
     * Play the current song.
     */
    fun play() {
        player.play()
        update()
    }

    /**
     * Use to remove the current song from all the lists and play the next one.
     * Used if there is a problem with the playback of the current song.
     */
    fun skipAndRemoveCurrentSong() {
        if (_currentMusic == null) return

        val currentIndex = getIndexOfCurrentMusic()
        val nextMusic = getNextMusic(currentIndex)

        _playedList.removeIf{ it.musicId == _currentMusic!!.musicId }
        _initialList.removeIf { it.musicId == _currentMusic!!.musicId }

        if (_playedList.isEmpty()) stopPlayback()
        else {
            nextMusic?.let {
                setAndPlayMusic(it)
            }
        }
    }

    /**
     * Launch a duration job, used for updating the UI to indicate the current position
     * in the played music.
     */
    protected fun launchDurationJob() {
        durationJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                playerViewModel?.handler?.currentMusicPosition = currentMusicPosition
            }
        }
    }

    /**
     * Release the duration job.
     */
    protected fun releaseDurationJob() {
        if (durationJob != null) {
            durationJob?.cancel()
            durationJob = null
        }
    }

    /**
     * Retrieve the index of the current played music.
     * Return -1 if the current music is null or if it is not found in the current playlist
     */
    private fun getIndexOfCurrentMusic(): Int {
        return if (_currentMusic == null) {
            -1
        } else {
            _playedList.indexOf(_playedList.find { it.musicId == _currentMusic!!.musicId })
        }
    }

    /**
     * Retrieve the next music in the current playlist.
     * Return null if nothing is found.
     * Return the first music if we are at the end of the playlist.
     */
    private fun getNextMusic(currentIndex: Int): Music? {
        return if (_playedList.isNotEmpty()) _playedList[(currentIndex + 1) % _playedList.size] else null
    }

    /**
     * Retrieve the previous music in the current playlist.
     * Return null if nothing is found.
     * Return the last music if we are at the start of the playlist.
     */
    private fun getPreviousMusic(currentIndex: Int): Music? {
        return if (_playedList.isNotEmpty()) {
            if (currentIndex == 0) {
                _playedList.last()
            } else {
                _playedList[currentIndex - 1]
            }
        } else {
            null
        }
    }

    /**
     * Play the next song in queue.
     */
    fun next() {
        val currentIndex = getIndexOfCurrentMusic()
        val nextMusic = getNextMusic(currentIndex)

        nextMusic?.let {
            player.pause()
            setAndPlayMusic(it)
        }
    }

    /**
     * Play the previous song in queue.
     */
    fun previous() {
        val currentIndex = getIndexOfCurrentMusic()
        val previousMusic = getPreviousMusic(currentIndex)

        previousMusic?.let {
            player.pause()
            setAndPlayMusic(it)
        }
    }

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int) {
        player.seekToPosition(position)
        update()
    }

    /**
     * Load the current music of the player view model.
     * The music will not be played.
     */
    fun onlyLoadMusic() {
        player.onlyLoadMusic()
        update()
    }

    /**
     * Initialize the player and the view from a saved music list (from the last app session).
     */
    abstract fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>)

    /**
     * Initialize the MusicPlayerManager.
     * It must be used when needing to start the playback.
     * It is not meant to be used when the class itself is initialized.
     */
    abstract fun initializeMusicPlayerManager(isFromSavedList: Boolean)

    /**
     * Set and play a Music.
     */
    abstract fun setAndPlayMusic(music: Music)

    /**
     * Stop the playback.
     * If will stop the service, dismiss the player and reset the player view model data.
     */
    abstract fun stopPlayback()

    /**
     * Use to update itself.
     */
    abstract fun update()
}