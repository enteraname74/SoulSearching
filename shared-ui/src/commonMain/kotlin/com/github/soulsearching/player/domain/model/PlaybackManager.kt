package com.github.soulsearching.player.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.reflect.KFunction1

/**
 * Used to manage the music playback of the application.
 */
abstract class PlaybackManager(
    protected val settings: SoulSearchingSettings
) {
    private var callback: Callback = object : Callback {}

    var retrieveCoverMethod: (UUID?) -> ImageBitmap? = {_ -> null}
    var updateNbPlayed: (UUID) -> Unit = {_ -> }

    /**
     * The initial list, used by the playback manager.
     */
    protected var _initialList: ArrayList<Music> = ArrayList()

    /**
     * The played list, used by the player and modified by the user (adding song...).
     */
    protected var _playedList: ArrayList<Music> = ArrayList()
    val playedList: List<Music>
        get() = _playedList

    /**
     * The id of the current played list.
     */
    private var _playedListId: UUID? = null
    val playedListId: UUID?
        get() = _playedListId

    /**
     * If the played list is the main one with all songs.
     */
    private var _isMainPlaylist: Boolean = false
    val isMainPlaylist: Boolean
        get() = _isMainPlaylist

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
     * Retrieve the index of the current played music.
     * Return -1 if the current music is null or if it is not found in the current playlist
     */
    val currentMusicIndex: Int
        get() = if (currentMusic == null) -1
            else _playedList.indexOf(_playedList.find { it.musicId == currentMusic!!.musicId })


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
     * Used to update frequently the current position in the duration
     * of the played music.
     */
    private var durationJob: Job? = null

    private var _playerMode = PlayerMode.NORMAL
    private var isChangingPlayerMode = false
    private var _currentMusicCover: ImageBitmap? = null
    val currentMusicCover: ImageBitmap?
        get() = _currentMusicCover

    /**
     * Reset all information related to the playback.
     */
    protected fun releasePlaybackManagerInformation() {
        _initialList = arrayListOf()
        _playedList = arrayListOf()
        _currentMusic = null
        _currentMusicCover = null
        _playedListId = null
        _isMainPlaylist = false
    }

    /**
     * Set the lists used by the player (played and initial list).
     */
    private fun setPlayerLists(musics: List<Music>) {
        _initialList = ArrayList(musics)
        _playedList = ArrayList(musics)
        callback.onPlayedListUpdated(
            playedList = _playedList
        )
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
     * Play the current song.
     */
    fun pause() {
        player.pause()
        update()
    }

    /**
     * Use to remove the current song from all the lists and play the next one.
     * Used if there is a problem with the playback of the current song.
     */
    fun skipAndRemoveCurrentSong() {
        if (_currentMusic == null) return

        val nextMusic = getNextMusic(currentMusicIndex)

        _playedList.removeIf { it.musicId == _currentMusic!!.musicId }
        _initialList.removeIf { it.musicId == _currentMusic!!.musicId }

        if (_playedList.isEmpty()) stopPlayback()
        else {
            nextMusic?.let {
                setAndPlayMusic(it)
            }
        }
    }

    /**
     * Remove a song from the current playlist.
     * If no songs are left in the played list, the playback will stop.
     */
    fun removeSongFromPlayedPlaylist(musicId: UUID) {
        _playedList.removeIf { it.musicId == musicId }

        // If no songs is left in the queue, stop playing :
        if (_playedList.isEmpty()) {
            stopPlayback()
        } else {
            // If same music than the one played, play next song :
            currentMusic?.let {
                if (it.musicId.compareTo(musicId) == 0) {
                    // We place ourself in the previous music :
                    _currentMusic = _playedList[(currentMusicIndex) % _playedList.size]
                    next()
                }
            }
        }
        settings.saveCurrentMusicInformation(
            currentMusicIndex = currentMusicIndex,
            currentMusicPosition = currentMusicPosition
        )
        update()
    }

    /**
     * Launch a duration job, used for updating the UI to indicate the current position
     * in the played music.
     */
    private fun launchDurationJob() {
        durationJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                callback.onCurrentMusicPositionChanged(
                    position = currentMusicPosition
                )
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
        val nextMusic = getNextMusic(currentMusicIndex)

        nextMusic?.let {
            player.pause()
            setAndPlayMusic(it)
        }
    }

    /**
     * Play the previous song in queue.
     */
    fun previous() {
        val previousMusic = getPreviousMusic(currentMusicIndex)

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
     * Update information of a song in the initial list, current list and the current music if it's the same.
     */
    fun updateMusic(music: Music) {
        _currentMusic?.let {
            if (it.musicId.compareTo(music.musicId) == 0) {
                _currentMusic = music
            }
        }

        val indexCurrent = _playedList.indexOfFirst { it.musicId == music.musicId }
        if (indexCurrent != -1) {
            _playedList[indexCurrent] = music
        }

        val indexInitial = _initialList.indexOfFirst { it.musicId == music.musicId }
        if (indexInitial != -1) {
            _initialList[indexInitial] = music
        }
        update()
    }

    /**
     * Update the cover of the current played music.
     */
    fun updateCover(cover: ImageBitmap?) {
        _currentMusicCover = cover
        callback.onCurrentMusicCoverChanged(
            cover = _currentMusicCover
        )
    }

    /**
     * Define the current music cover and the current color palette from the cover.
     */
    fun defineCoverAndPaletteFromCoverId(coverId: UUID?) {
        _currentMusicCover = retrieveCoverMethod(coverId)
        callback.onCurrentMusicCoverChanged(
            cover = _currentMusicCover
        )
    }

    /**
     * Define the current music.
     */
    private fun setNewCurrentMusicInformation(music: Music?) {
        _currentMusic = music
        callback.onCurrentPlayedMusicChanged(
            music = _currentMusic
        )
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
    }

    /**
     * Add a music to play next.
     * Does nothing if we try to add the current music.
     * If the playlist is empty (nothing is playing), we load the music.
     * It will remove the previous apparition of the music if there was one.
     */
    fun addMusicToPlayNext(music: Music) {
        // If same music than the one played, does nothing :
        currentMusic?.let {
            if (music.musicId.compareTo(it.musicId) == 0) {
                return
            }
        }
        // If the current playlist is empty, we load the music :
        if (_playedList.isEmpty()) {
            _playedList.add(music)
            setNewCurrentMusicInformation(music)
            onlyLoadMusic()
        }

        // We make sure to remove the music if it's already in the playlist :
        _playedList.removeIf { it.musicId == music.musicId }

        // Finally, we add the new next music :
        _playedList.add(currentMusicIndex + 1, music)

        callback.onPlayedListUpdated(
            playedList = _playedList
        )

        settings.saveCurrentMusicInformation(
            currentMusicIndex = currentMusicIndex,
            currentMusicPosition = currentMusicPosition
        )
    }

    /**
     * Define the current music from a given index.
     * If the index is out of bound, the current music will be the first one of the playlist.
     */
    private fun setMusicFromIndex(index: Int) {
        if (_playedList.isNotEmpty()) {
            _currentMusic = if (index <= _playedList.lastIndex) {
                _playedList[Integer.max(0, index)]
            } else {
                _playedList[0]
            }
        }
        callback.onCurrentPlayedMusicChanged(
            music = _currentMusic
        )
    }

    /**
     * Check if a music is the same as the current one.
     * If there is no current music, return false.
     */
    fun isSameMusicAsCurrentPlayedOne(musicId: UUID): Boolean {
        return if (currentMusic == null) {
            false
        } else {
            currentMusic!!.musicId.compareTo(musicId) == 0
        }
    }

    /**
     * Shuffle the current playlist and place the current music at first place in the list.
     */
    private fun shuffleCurrentList(listToShuffle: java.util.ArrayList<Music>) {
        val tmpList = listToShuffle.map { it.copy() } as ArrayList<Music>
        tmpList.shuffle()
        _currentMusic?.let { music ->
            tmpList.removeIf { it.musicId == music.musicId }
            tmpList.add(0, music)
        }
        setCurrentPlayedList(tmpList)
    }

    /**
     * Check if a playlist is the same as the current one.
     * We can also check with a isMainPlaylist value (the main playlist, with all songs, does not have
     * a UUID).
     */
    fun isSamePlaylist(isMainPlaylist: Boolean, playlistId: UUID?): Boolean {
        if (playlistId == null && this._playedListId == null) {
            return isMainPlaylist == this._isMainPlaylist
        } else if (playlistId != null && this._playedListId != null) {
            return (playlistId.compareTo(_playedListId) == 0) && (isMainPlaylist == this._isMainPlaylist)
        }
        return false
    }

    /**
     * Define the player mode to use for the current playlist.
     */
    private fun setPlayerMode(playerMode: PlayerMode){
        _playerMode = playerMode
        callback.onPlayerModeChanged(
            playerMode = _playerMode
        )
    }

    /**
     * Define the current played list.
     */
    private fun setCurrentPlayedList(playedList: ArrayList<Music>){
        _playedList = playedList
        callback.onPlayedListUpdated(
            playedList = _playedList
        )
    }

    /**
     * Initialize the player and the view from a saved music list (from the last app session).
     */
    open fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>) {
        setPlayerLists(savedMusicList)

        val index = settings.getInt(SoulSearchingSettings.PLAYER_MUSIC_INDEX_KEY, -1)
        val position = settings.getInt(SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY, 0)

        setMusicFromIndex(index)
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
        seekToPosition(position)

        setPlayerMode(settings.getPlayerMode())
        onlyLoadMusic()
    }

    /**
     * Set and play a Music.
     */
    open fun setAndPlayMusic(music: Music) {
        _currentMusic = music
        player.setMusic(music)
        player.launchMusic()
        updateNbPlayed(music.musicId)
        update()
    }

    /**
     * Force the player mode the normal.
     * The current playlist will be reset to be like the music list passed in parameter.
     */
    private fun forcePlayerModeToNormal(musicList: java.util.ArrayList<Music>) {
        if (!isChangingPlayerMode) {
            isChangingPlayerMode = true
            setPlayerMode(PlayerMode.NORMAL)
            setCurrentPlayedList(musicList.map { it.copy() } as java.util.ArrayList<Music>)

            settings.saveCurrentMusicInformation(
                currentMusicIndex = currentMusicIndex,
                currentMusicPosition = currentMusicPosition
            )
            isChangingPlayerMode = false
        }
    }

    /**
     * Change the player mode.
     */
    fun changePlayerMode() {
        if (!isChangingPlayerMode) {
            isChangingPlayerMode = true
            when (_playerMode) {
                PlayerMode.NORMAL -> {
                    // to shuffle mode :
                    shuffleCurrentList(_playedList)
                    setPlayerMode(playerMode =  PlayerMode.SHUFFLE)
                }
                PlayerMode.SHUFFLE -> {
                    // to loop mode :
                    _playedList = if (currentMusic != null) arrayListOf(currentMusic!!) else ArrayList()
                    _playedListId = null
                    callback.onPlayedListUpdated(
                        playedList = _playedList
                    )
                    setPlayerMode(playerMode =  PlayerMode.LOOP)
                }
                PlayerMode.LOOP -> {
                    // to normal mode :
                    _playedList = _initialList.map { it.copy() } as java.util.ArrayList<Music>
                    _playedListId = null
                    callback.onPlayedListUpdated(
                        playedList = _playedList
                    )
                    setPlayerMode(playerMode =  PlayerMode.NORMAL)
                }
            }
            settings.setPlayerMode(
                key = SoulSearchingSettings.PLAYER_MODE_KEY,
                value = _playerMode
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = currentMusicIndex,
                currentMusicPosition = currentMusicPosition
            )
            isChangingPlayerMode = false
        }
    }

    /**
     * Define the current playlist and music.
     * Primarily used when clicking on a music.
     */
    fun setCurrentPlaylistAndMusic(
        music: Music,
        playlist: ArrayList<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        // When selecting a music manually, we force the player mode to normal:
        forcePlayerModeToNormal(playlist)

        // If it's the same music of the same playlist, does nothing
        if (isSameMusicAsCurrentPlayedOne(music.musicId) && isSamePlaylist(
                isMainPlaylist,
                playlistId
            ) && !isForcingNewPlaylist
        ) {
            return
        }

        val shouldForcePlaylistOrNewPlaylist = !isSamePlaylist(isMainPlaylist, playlistId) || isForcingNewPlaylist
        val notSameMusic = !isSameMusicAsCurrentPlayedOne(music.musicId)

        if (shouldForcePlaylistOrNewPlaylist) {
            setPlayerLists(_playedList)
            this@PlaybackManager._isMainPlaylist = isMainPlaylist
            settings.saveCurrentMusicInformation(
                currentMusicIndex = currentMusicIndex,
                currentMusicPosition = currentMusicPosition
            )
        }

        if (notSameMusic) {
            setNewCurrentMusicInformation(music)
        }

        setAndPlayMusic(music)
    }

    /**
     * Remove a music from the playlist if the playlist is the same as a specified one.
     */
    fun removeMusicIfSamePlaylist(musicId: UUID, playlistId: UUID?) {
        if (playlistId == null && _playedListId == null) {
            removeSongFromPlayedPlaylist(musicId)
        } else if (playlistId != null && _playedListId != null) {
            if (playlistId.compareTo(_playedListId) == 0) {
                removeSongFromPlayedPlaylist(musicId)
            }
        }
    }

    /**
     * Play a playlist in shuffle and save it.
     */
    fun playShuffle(playlist: ArrayList<Music>, savePlayerListMethod: KFunction1<ArrayList<UUID>, Unit>) {
        _playedListId = null
        _isMainPlaylist = false

        setPlayerLists(playlist.shuffled())
        savePlayerListMethod(_playedList.map { it.musicId } as ArrayList<UUID>)

        setPlayerMode(PlayerMode.NORMAL)
        settings.setPlayerMode(
            key = SoulSearchingSettings.PLAYER_MODE_KEY,
            value = _playerMode
        )
        settings.saveCurrentMusicInformation(
            currentMusicIndex = currentMusicIndex,
            currentMusicPosition = currentMusicPosition
        )

        setNewCurrentMusicInformation(_playedList[0])
        setAndPlayMusic(_playedList[0])
    }

    /**
     * Define the callback to use to respond to playback events.
     */
    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    /**
     * Stop the playback.
     * If will stop the service, dismiss the player and reset the player view model data.
     */
    open fun stopPlayback() {
        releaseDurationJob()
        releasePlaybackManagerInformation()
        update()
    }

    /**
     * Use to update itself.
     */
    open fun update() {
        if (durationJob == null && isPlaying) {
            launchDurationJob()
        }
        defineCoverAndPaletteFromCoverId(_currentMusic?.coverId)
        callback.onCurrentPlayedMusicChanged(music = _currentMusic)
        callback.onPlayingStateChanged(isPlaying = isPlaying)
        callback.onPlayedListUpdated(playedList = _playedList)
        callback.onCurrentMusicPositionChanged(position = currentMusicPosition)
    }

    companion object {

        /**
         * Callbacks used by the playback manager.
         * Primarily used to update view related to playback state.
         */
        interface Callback {

            /**
             * Emitted when the played list has been updated.
             */
            fun onPlayedListUpdated(playedList: List<Music>) = Unit

            /**
             * Emitted when the player mode has changed.
             */
            fun onPlayerModeChanged(playerMode: PlayerMode) = Unit

            /**
             * Emitted when the current played music changed or was dismiss (null value emitted)
             */
            fun onCurrentPlayedMusicChanged(music: Music?) = Unit

            /**
             * Emitted when the position in the current played music has changed.
             */
            fun onCurrentMusicPositionChanged(position: Int) = Unit

            /**
             * Emitted when the playing state changed.
             */
            fun onPlayingStateChanged(isPlaying: Boolean) = Unit

            /**
             * Emitted when the cover of the current played music changed.
             */
            fun onCurrentMusicCoverChanged(cover: ImageBitmap?) = Unit
        }
    }
}