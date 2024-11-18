package com.github.enteraname74.soulsearching.features.playback.list

import com.github.enteraname74.domain.ext.getFirstsOrMax
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.soulsearching.features.playback.SoulSearchingPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.util.*

internal class PlaybackListManager(
    private val playbackCallback: PlaybackListCallbacks,
    private val player: SoulSearchingPlayer,
    private val settings: SoulSearchingSettings,
    private val playerMusicRepository: PlayerMusicRepository,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
) {
    private val _state: MutableStateFlow<PlaybackListState> = MutableStateFlow(PlaybackListState.NoData)
    val state: StateFlow<PlaybackListState> = _state.asStateFlow()

    private var updateMusicNbPlayedJob: Job? = null

    /**
     * Used to save the currently played list in the database for future uses after a relaunch
     * of the app.
     */
    private var playedListSavingJob: Job? = null

    private suspend fun <R> withDataState(block: suspend PlaybackListState.Data.() -> R): R? =
        (_state.value as? PlaybackListState.Data)?.let {
            block(it)
        }

    /**
     * Save the played list to the database.
     */
    private fun savePlayedList(list: List<Music>) {
        playedListSavingJob?.cancel()
        playedListSavingJob = CoroutineScope(Dispatchers.IO).launch {
            playerMusicRepository.deleteAll()
            playerMusicRepository.upsertAll(
                playlist = list.map { PlayerMusic(playerMusicId = it.musicId) }
            )
        }
    }

    /**
     * Add a music to play next.
     * Does nothing if we try to add the current music.
     * If the playlist is empty (nothing is playing), we load the music.
     * It will remove the previous apparition of the music if there was one.
     */
    suspend fun addMusicToPlayNext(music: Music) {
        if (_state.value is PlaybackListState.NoData) {
            // We will initialize the player:
            val singletonList = listOf(music)

            init(
                musics = singletonList,
                currentMusic = music,
            )

            playbackCallback.onlyLoadMusic(music = music)
            savePlayedList(list = singletonList)
            settings.saveCurrentMusicInformation(
                currentMusicIndex = 0,
                currentMusicPosition = 0,
            )
            return
        }

        withDataState {
            // If same music than the one played, does nothing :
            if (music.musicId.compareTo(currentMusic.musicId) == 0) {
                return@withDataState
            }

            // We make sure to remove the music if it's already in the playlist :
            val updatedPlayedList = ArrayList(playedList)
            updatedPlayedList.removeIf { it.musicId == music.musicId }

            // If the current playlist is empty, we load the music :
            if (updatedPlayedList.isEmpty()) {
                updatedPlayedList.add(music)
                init(
                    musics = updatedPlayedList,
                    currentMusic = music,
                )
                playbackCallback.onlyLoadMusic(music = music)
            } else {
                // Finally, we add the new next music :
                val nextSongIndex = updatedPlayedList
                    .indexOf(
                        updatedPlayedList.find { it.musicId == currentMusic.musicId }
                    ) + 1
                if (nextSongIndex > updatedPlayedList.lastIndex) {
                    updatedPlayedList.add(music)
                } else {
                    updatedPlayedList.add(nextSongIndex, music)
                }

                _state.value = this.copy(
                    playedList = updatedPlayedList,
                )
                matchInitialListToPlayedListIfNormalPlayerMode()
            }

            savePlayedList(list = updatedPlayedList)
            settings.saveCurrentMusicInformation(
                currentMusicIndex = currentMusicIndex,
                currentMusicPosition = playbackCallback.getMusicPosition(),
            )
        }
    }

    suspend fun addMultipleMusicsToPlayNext(musics: List<Music>) {
        /*
        If we have not initialized the player and if we have more than 2 songs, we need to do the following :
        - Add the first song
        - Add all the other songs in reverse.
         */
        if ((_state.value is PlaybackListState.NoData || (_state.value as? PlaybackListState.Data)?.playedList?.isEmpty() == true) && musics.size > 2) {
            addMusicToPlayNext(music = musics[0])
            musics.subList(1, musics.size).reversed().forEach { music ->
                println("MUSIC TO ADD: $music")
                addMusicToPlayNext(music = music)
            }
        }

        if (_state.value is PlaybackListState.Data) {
            musics.reversed().forEach { music ->
                addMusicToPlayNext(music = music)
            }
        }
    }

    /**
     * Change the player mode.
     */
    suspend fun changePlayerMode() {
        withDataState {
            val playerMode: PlayerMode = when (playerMode) {
                PlayerMode.Normal -> {
                    // to shuffle mode :
                    val tmpList = ArrayList(playedList)
                    tmpList.shuffle()
                    currentMusic.let { music ->
                        tmpList.removeIf { it.musicId == music.musicId }
                        tmpList.add(0, music)
                    }

                    val mode = PlayerMode.Shuffle
                    _state.value = this.copy(
                        playerMode = mode,
                        playedList = tmpList,
                    )
                    mode
                }

                PlayerMode.Shuffle -> {
                    // to loop mode :
                    val mode = PlayerMode.Loop
                    _state.value = this.copy(
                        playedList = listOf(currentMusic),
                        playerMode = mode,
                        playlistId = null,
                    )
                    mode
                }

                PlayerMode.Loop -> {
                    // to normal mode :
                    val mode = PlayerMode.Normal
                    _state.value = this.copy(
                        playlistId = null,
                        playedList = initialList.map { it.copy() },
                        playerMode = mode,
                    )
                    mode
                }
            }
            settings.set(
                key = SoulSearchingSettingsKeys.Player.PLAYER_MODE_KEY.key,
                value = playerMode
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = currentMusicIndex,
                currentMusicPosition = playbackCallback.getMusicPosition(),
            )
            savePlayedList(
                list = (_state.value as? PlaybackListState.Data)?.playedList ?: emptyList()
            )
        }
    }

    /**
     * Set the lists used by the player (played and initial list).
     */
    fun init(
        musics: List<Music>,
        currentMusic: Music,
    ) {
        _state.value = PlaybackListState.Data(
            initialList = ArrayList(musics),
            playedList = ArrayList(musics),
            currentMusic = currentMusic,
            playerMode = PlayerMode.Normal,
            minimisePlayer = true,
        )
    }

    /**
     * Init the lists used by the player from the saved one in the db
     */
    suspend fun init() {
        val list = playerMusicRepository.getAll().first().mapNotNull { it.music }
        val index = settings.get(SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_INDEX_KEY)
        val playerMode = settings.get(SoulSearchingSettingsKeys.Player.PLAYER_MODE_KEY)
        val position = settings.get(SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_POSITION_KEY)

        if (list.isEmpty()) {
            _state.value = PlaybackListState.NoData
        } else {
            val currentMusic: Music = list.getOrNull(index) ?: list.first()
            _state.value = PlaybackListState.Data(
                playedList = list,
                initialList = list,
                currentMusic = currentMusic,
                playerMode = playerMode,
                minimisePlayer = true,
            )
            playbackCallback.onlyLoadMusic(
                seekTo = position,
                music = currentMusic,
            )
        }
    }

    /**
     * Make the initial list the same as the played list.
     * The match will only occur if the player mode is set to NORMAL.
     */
    private suspend fun matchInitialListToPlayedListIfNormalPlayerMode() {
        withDataState {
            if (playerMode == PlayerMode.Normal) {
                _state.value = this.copy(
                    initialList = playedList.map { it.copy() },
                )
            }
        }
    }

    /**
     * Check if a music is the same as the current one.
     * If there is no current music, return false.
     */
    fun isSameMusicAsCurrentPlayedOne(musicId: UUID): Boolean = runBlocking {
        withDataState {
            runBlocking {
                currentMusic.musicId.compareTo(musicId) == 0
            }
        } ?: false
    }

    /**
     * Check if a playlist is the same as the current one.
     * We can also check with a isMainPlaylist value (the main playlist, with all songs, does not have
     * a UUID).
     */
    private suspend fun isSamePlaylist(isMainPlaylist: Boolean, playlistId: UUID?): Boolean = withDataState {
        return@withDataState if (playlistId == null && this.playlistId == null) {
            isMainPlaylist == this.isMainPlaylist
        } else if (playlistId != null && this.playlistId != null) {
            (playlistId.compareTo(this.playlistId) == 0) && (isMainPlaylist == this.isMainPlaylist)
        } else {
            false
        }
    } ?: false

    /**
     * Remove songs from the current playlist.
     * If no songs are left in the played list, the playback will stop.
     */
    suspend fun removeSongsFromPlayedPlaylist(musicIds: List<UUID>) {
        withDataState {
            val actualIndex = currentMusicIndex
            val playedList = ArrayList(playedList)

            playedList.removeIf { it.musicId in musicIds }
            matchInitialListToPlayedListIfNormalPlayerMode()

            // If no songs is left in the queue, stop playing :
            if (playedList.isEmpty()) {
                playbackCallback.stopPlayback()
            } else {
                // If the current song is in the deleted one, we play the next song.
                if (currentMusic.musicId in musicIds) {
                    // We first place ourselves in the next music :
                    val newCurrentSong = if (actualIndex > playedList.lastIndex) {
                        playedList[0]
                    } else {
                        playedList[actualIndex]
                    }

                    _state.value = this.copy(playedList = playedList)
                    playbackCallback.setAndPlayMusic(newCurrentSong)
                } else {
                    _state.value = this.copy(
                        playedList = playedList,
                    )
                    settings.saveCurrentMusicInformation(
                        currentMusicIndex = playedList.indexOf(currentMusic),
                        currentMusicPosition = playbackCallback.getMusicPosition(),
                    )
                }
                savePlayedList(list = playedList)
            }
        }
    }

    /**
     * Retrieve the next music in the current playlist.
     * Return null if nothing is found.
     * Return the first music if we are at the end of the playlist.
     */
    suspend fun getNextMusic(): Music? = withDataState {
        if (playedList.isNotEmpty()) playedList[(currentMusicIndex + 1) % playedList.size] else null
    }

    /**
     * Retrieve the previous music in the current playlist.
     * Return null if nothing is found.
     * Return the last music if we are at the start of the playlist.
     */
    suspend fun getPreviousMusic(): Music? = withDataState {
        if (playedList.isNotEmpty()) {
            if (currentMusicIndex == 0) {
                playedList.last()
            } else {
                playedList[currentMusicIndex - 1]
            }
        } else {
            null
        }
    }

    suspend fun setAndPlayMusic(music: Music) {
        withDataState {
            _state.value = this.copy(
                currentMusic = music,
            )
//            settings.set(
//                key = SoulSearchingSettingsKeys.Player.PLAYER_MUSIC_INDEX_KEY.key,
//                value = playedList.indexOfFirst { it.musicId == music.musicId }
//            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = playedList.indexOfFirst { it.musicId == music.musicId },
                currentMusicPosition = 0,
            )
            player.setMusic(music)
            player.launchMusic()

            updateMusicNbPlayedJob?.cancel()
            updateMusicNbPlayedJob = CoroutineScope(Dispatchers.IO).launch {
                delay(WAIT_TIME_BEFORE_UPDATE_NB_PLAYED)
                updateMusicNbPlayedUseCase(musicId = music.musicId)
            }
        }
    }

    /**
     * Define the current playlist and music.
     * Primarily used when clicking on a music.
     */
    suspend fun setCurrentPlaylistAndMusic(
        music: Music,
        musicList: List<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        // If it's the same music of the same playlist, does nothing
        if (isSameMusicAsCurrentPlayedOne(music.musicId) && isSamePlaylist(
                isMainPlaylist,
                playlistId
            ) && !isForcingNewPlaylist
        ) {
            return
        }

//        val shouldForcePlaylistOrNewPlaylist =
//            !isSamePlaylist(isMainPlaylist, playlistId) || isForcingNewPlaylist
//        val notSameMusic = !isSameMusicAsCurrentPlayedOne(music.musicId)

//        if (shouldForcePlaylistOrNewPlaylist) {
//            setPlayerLists(playedList)
//            this@PlaybackManager.isMainPlaylist = isMainPlaylist
//            settings.saveCurrentMusicInformation(
//                currentMusicIndex = currentMusicIndex,
//                currentMusicPosition = playbackManager.getMusicPosition()
//            )
//        }

        // When selecting a music manually, we force the player mode to normal:
        _state.value = PlaybackListState.Data(
            isMainPlaylist = isMainPlaylist,
            playedList = musicList,
            initialList = musicList,
            playlistId = playlistId,
            currentMusic = music,
            playerMode = PlayerMode.Normal,
        ).also { dataState ->
            settings.saveCurrentMusicInformation(
                currentMusicIndex = dataState.currentMusicIndex,
                currentMusicPosition = 0,
            )
        }

        setAndPlayMusic(music)
        savePlayedList(musicList)
    }

    /**
     * Play a playlist in shuffle and save it.
     */
    suspend fun playShuffle(musicList: List<Music>) {
        if (musicList.isEmpty()) return

        val playerList = musicList.shuffled()

        _state.value = PlaybackListState.Data(
            initialList = playerList,
            playedList = playerList,
            playerMode = PlayerMode.Normal,
            playlistId = null,
            isMainPlaylist = false,
            currentMusic = playerList[0],
        ).also { dataState ->
            settings.saveCurrentMusicInformation(
                currentMusicIndex = dataState.currentMusicIndex,
                currentMusicPosition = 0,
            )
        }

        settings.set(
            key = SoulSearchingSettingsKeys.Player.PLAYER_MODE_KEY.key,
            value = PlayerMode.Normal,
        )

        setAndPlayMusic(playerList[0])
        savePlayedList(list = playerList)
    }

    suspend fun removeCurrentSongInAllLists() {
        withDataState {
            val newPlayedList = ArrayList(playedList)
            val newInitialList = ArrayList(initialList)

            newPlayedList.removeIf { it.musicId == currentMusic.musicId }
            newInitialList.removeIf { it.musicId == currentMusic.musicId }

            _state.value = this.copy(
                playedList = newPlayedList,
                initialList = newInitialList,
            )
        }
    }

    suspend fun updateMusic(music: Music) {
        withDataState {
            val newCurrentSong = if (currentMusic.musicId.compareTo(music.musicId) == 0) {
                music
            } else {
                currentMusic
            }

            val indexCurrent = playedList.indexOfFirst { it.musicId == music.musicId }
            val newPlayedList = if (indexCurrent != -1) {
                val tmpList = ArrayList(playedList)
                tmpList[indexCurrent] = music
                tmpList
            } else {
                playedList
            }

            val indexInitial = initialList.indexOfFirst { it.musicId == music.musicId }
            val newInitialList = if (indexInitial != -1) {
                val tmpList = ArrayList(initialList)
                tmpList[indexInitial] = music
                tmpList
            } else {
                initialList
            }

            _state.value = this.copy(
                currentMusic = newCurrentSong,
                playedList = newPlayedList,
                initialList = newInitialList,
            )
        }
    }

    fun hasData(): Boolean =
        (_state.value as? PlaybackListState.Data)?.playedList?.isNotEmpty() == false

    /**
     * Reset the saved played list in the db.
     */
    suspend fun clear(resetPlayedList: Boolean = true) {
        if (resetPlayedList) {
            playerMusicRepository.deleteAll()
            settings.saveCurrentMusicInformation(
                currentMusicIndex = -1,
                currentMusicPosition = 0
            )
        }
        _state.value = PlaybackListState.NoData
    }

    suspend fun playSoulMix(musicLists: List<List<Music>>) {
        val totalByList: Int = settings.get(SoulSearchingSettingsKeys.Player.SOUL_MIX_TOTAL_BY_LIST)

        val musicList: List<Music> = buildList {
            musicLists.forEach { initialList ->
                addAll(initialList.shuffled().getFirstsOrMax(total = totalByList))
            }
        }.shuffled()

        if (musicList.isEmpty()) return

        _state.value = PlaybackListState.Data(
            playedList = ArrayList(musicList),
            initialList = ArrayList(musicList),
            currentMusic = musicList[0],
            playlistId = null,
            isMainPlaylist = false,
            playerMode = PlayerMode.Normal,
        ).also {
            settings.set(
                key = SoulSearchingSettingsKeys.Player.PLAYER_MODE_KEY.key,
                value = it.playerMode,
            )
            settings.saveCurrentMusicInformation(
                currentMusicIndex = 0,
                currentMusicPosition = 0,
            )
        }

        setAndPlayMusic(music = musicList[0])
        savePlayedList(list = musicList)
    }

    companion object {
        private const val WAIT_TIME_BEFORE_UPDATE_NB_PLAYED: Long = 3_000
    }
}