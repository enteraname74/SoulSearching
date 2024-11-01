package com.github.enteraname74.soulsearching.features.playback.manager

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.soulsearching.features.playback.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.features.playback.list.PlaybackListCallbacks
import com.github.enteraname74.soulsearching.features.playback.list.PlaybackListManager
import com.github.enteraname74.soulsearching.features.playback.list.PlaybackListState
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.progressjob.PlaybackProgressJob
import com.github.enteraname74.soulsearching.features.playback.progressjob.PlaybackProgressJobCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

abstract class PlaybackManager : KoinComponent {

    private val playerMusicRepository: PlayerMusicRepository by inject()
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase by inject()
    private val settings: SoulSearchingSettings by inject()

    abstract val player: SoulSearchingPlayer
    abstract val notification: SoulSearchingNotification

    private val playbackProgressJob: PlaybackProgressJob = PlaybackProgressJob(
        settings = settings,
        callback = object : PlaybackProgressJobCallbacks {
            override fun isPlaying(): Boolean =
                player.isPlaying()

            override fun getMusicPosition(): Int =
                this@PlaybackManager.getMusicPosition()
        },
    )
    private val playbackListManager: PlaybackListManager by lazy {
        PlaybackListManager(
            settings = settings,
            playbackCallback = object : PlaybackListCallbacks {
                override suspend fun onlyLoadMusic(seekTo: Int, music: Music) =
                    this@PlaybackManager.onlyLoadMusic(seekTo, music)

                override suspend fun getMusicPosition(): Int =
                    this@PlaybackManager.getMusicPosition()

                override suspend fun stopPlayback() =
                    this@PlaybackManager.stopPlayback()

                override suspend fun next() =
                    this@PlaybackManager.next()

                override suspend fun setAndPlayMusic(music: Music) =
                    this@PlaybackManager.setAndPlayMusic(music)
            },
            player = player,
            playerMusicRepository = playerMusicRepository,
            updateMusicNbPlayedUseCase = updateMusicNbPlayedUseCase,
        )
    }

    val mainState: StateFlow<PlaybackManagerState> by lazy {
        combine(
            playbackListManager.state,
            player.state,
            _currentCoverState,
        ) { listState, playerState, cover ->
            when (listState) {
                is PlaybackListState.NoData -> {
                    notification.dismissNotification()
                    PlaybackManagerState.Stopped
                }
                is PlaybackListState.Data -> {
                    val state = PlaybackManagerState.Data(
                        currentMusic = listState.currentMusic,
                        currentMusicIndex = listState.currentMusicIndex,
                        playedList = listState.playedList,
                        isPlaying = playerState,
                        playerMode = listState.playerMode,
                        minimisePlayer = listState.minimisePlayer,
                    )
                    playbackProgressJob.launchDurationJobIfNecessary()
                    notification.updateNotification(
                        playbackManagerState = state,
                        cover = cover,
                    )
                    state
                }
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = PlaybackManagerState.Stopped,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSong: StateFlow<Music?> by lazy {
        playbackListManager.state.mapLatest { state ->
            when (state) {
                is PlaybackListState.Data -> state.currentMusic
                PlaybackListState.NoData -> null
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
    }

    val currentSongProgressionState: StateFlow<Int> = playbackProgressJob.state

    private val _currentCoverState: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    val currentCoverState: StateFlow<ImageBitmap?> = _currentCoverState.asStateFlow()

    fun initFromSavedData() {
        CoroutineScope(Dispatchers.IO).launch {
            playbackListManager.init()
        }
    }

    /**
     * Load the current music of the player view model.
     * The music will not be played.
     */
    fun onlyLoadMusic(seekTo: Int = 0, music: Music) {
        player.setMusic(music = music)
        player.onlyLoadMusic(seekTo = seekTo)
    }

    fun getMusicPosition(): Int =
        player.getMusicPosition()

    /**
     * Stop the playback.
     * It will stop the service, dismiss the player and reset the player view model data.
     */
    suspend fun stopPlayback(resetPlayedList: Boolean = true) {
        playbackProgressJob.releaseDurationJob()
        updateCover(cover = null)
        playbackListManager.clear(resetPlayedList = resetPlayedList)
        player.dismiss()
    }

    /**
     * Play or pause the player, depending on its current state.
     */
    fun togglePlayPause() {
        player.togglePlayPause()
        playbackProgressJob.launchDurationJobIfNecessary()
    }

    /**
     * Play the current song.
     */
    fun play() {
        player.play()
    }

    /**
     * Play the current song.
     */
    fun pause() {
        player.pause()
    }

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int) {
        player.seekToPosition(position)
        playbackProgressJob.launchDurationJobIfNecessary()
    }

    fun handleKeyEvent(event: KeyEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            if (event.isCtrlPressed && event.type == KeyEventType.KeyUp) {
                // If no music is being played, we do nothing
                if (mainState.value !is PlaybackManagerState.Data) return@launch

                when(event.key) {
                    Key.P -> togglePlayPause()
                    Key.B, Key.DirectionLeft -> previous()
                    Key.N, Key.DirectionRight -> next()
                    else -> { /*no-op*/ }
                }
            }
        }
    }

    /**
     * Play the next song in queue.
     */
    suspend fun next() {
        playbackListManager.getNextMusic()?.let {
            setAndPlayMusic(it)
        }
    }

    /**
     * Play the previous song in queue.
     */
    suspend fun previous() {
        playbackListManager.getPreviousMusic()?.let {
            setAndPlayMusic(it)
        }
    }

    suspend fun getNextMusic(): Music? =
        playbackListManager.getNextMusic()

    suspend fun getPreviousMusic(): Music? =
        playbackListManager.getPreviousMusic()

    suspend fun skipAndRemoveCurrentSong() {
        if (!playbackListManager.hasData()) return

        playbackListManager.getNextMusic()?.let { nextMusic ->
            playbackListManager.removeCurrentSongInAllLists()
            if (!playbackListManager.hasData()) {
                stopPlayback()
            } else {
                setAndPlayMusic(music = nextMusic)
            }
        }
    }

    suspend fun setAndPlayMusic(music: Music) {
        playbackProgressJob.setPosition(pos = 0)
        playbackListManager.setAndPlayMusic(music)
    }

    suspend fun updateMusic(music: Music) {
        playbackListManager.updateMusic(music)
    }

    suspend fun changePlayerMode() {
        playbackListManager.changePlayerMode()
    }

    suspend fun removeSongsFromPlayedPlaylist(musicIds: List<UUID>) {
        playbackListManager.removeSongsFromPlayedPlaylist(musicIds = musicIds)
    }

    suspend fun addMusicToPlayNext(music: Music) {
        playbackListManager.addMusicToPlayNext(music = music)
    }

    fun isSameMusicAsCurrentPlayedOne(musicId: UUID): Boolean =
        playbackListManager.isSameMusicAsCurrentPlayedOne(musicId = musicId)

    /**
     * Update the cover of the current played music.
     */
    fun updateCover(cover: ImageBitmap?) {
        _currentCoverState.value = cover
    }

    suspend fun playShuffle(musicList: List<Music>) {
        playbackListManager.playShuffle(musicList = musicList)
    }

    suspend fun playSoulMix(musicLists: List<List<Music>>) {
        playbackListManager.playSoulMix(musicLists = musicLists)
    }

    suspend fun setCurrentPlaylistAndMusic(
        music: Music,
        musicList: List<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        playbackListManager.setCurrentPlaylistAndMusic(
            music = music,
            musicList = musicList,
            playlistId = playlistId,
            isMainPlaylist = isMainPlaylist,
            isForcingNewPlaylist = isForcingNewPlaylist,
        )
    }
}