package com.github.enteraname74.soulsearching.features.playback.manager

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicInFavoritePlaylistUseCase
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class PlaybackManager(
    private val playerRepository: PlayerRepository,
    private val settings: SoulSearchingSettings,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
    private val coverRetriever: CoverRetriever,
) : KoinComponent, SoulSearchingPlayer.Listener {
    private val notification: SoulSearchingNotification by inject()
    private val player: SoulSearchingPlayer by inject()

    private val workScope = CoroutineScope(Dispatchers.IO)
    private var updateMusicNbPlayedJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentMusicFavoriteStatusState: Flow<Boolean> =
        playerRepository.getCurrentMusic().flatMapLatest { current ->
            current?.music?.musicId?.let {
                isMusicInFavoritePlaylistUseCase(musicId = it)
            } ?: flowOf(false)
        }

    // TODO PLAYER: improve this mess!
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSong: StateFlow<Music?> by lazy {
        playerRepository.getCurrentMusic().map { it?.music }
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = null,
            )
    }

    val currentSongProgressionState: Flow<Int> =
        playerRepository.getCurrentProgress()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentCover: Flow<ImageBitmap?> =
        playerRepository.getCurrentMusic().map { currentMusic ->
            currentMusic?.music?.cover?.let { coverRetriever.getCoverImageBitmap(it) }
        }

    val paginatedList: Flow<PagingData<Music>> = playerRepository.getAllPaginated()

    val state: Flow<PlaybackManagerState> =
        combine(
            playerRepository.getCurrentMusic(),
            playerRepository.getCurrentPlayedList(),
            currentCover,
            playerRepository.getSize(),
            playerRepository.getCurrentPosition(),
            currentMusicFavoriteStatusState,
            playerRepository.getNextMusic(),
            playerRepository.getPreviousMusic(),
        ) { array ->
            val currentMusic: Music? = (array[0] as PlayerMusic?)?.music
            val next: Music? = (array[6] as PlayerMusic?)?.music
            val previous: Music? = (array[7] as PlayerMusic?)?.music
            val currentPlayedList: PlayerPlayedList? = array[1] as PlayerPlayedList?

            if (currentMusic == null || currentPlayedList == null || currentPlayedList.state == PlayedListState.Cached) {
                PlaybackManagerState.Stopped
            } else {
                PlaybackManagerState.Data(
                    currentMusic = currentMusic,
                    next = next,
                    previous = previous,
                    isCurrentMusicInFavorite = array[5] as Boolean,
                    currentMusicIndex = ((array[4] as Int?) ?: 0) - 1,
                    listSize = array[3] as Int,
                    playerMode = currentPlayedList.mode,
                    isPlaying = currentPlayedList.state == PlayedListState.Playing,
                    currentState = currentPlayedList.state,
                )
            }
        }.distinctUntilChanged()

    private val notificationDataFlow: Flow<UpdateData?> =
        combine(
            playerRepository.getCurrentMusic(),
            playerRepository.getCurrentPlayedList(),
            currentCover,
            playerRepository.getSize(),
            playerRepository.getCurrentPosition(),
            currentMusicFavoriteStatusState,
        ) { array ->
            val currentMusic: Music? = (array[0] as PlayerMusic?)?.music
            val currentPlayedList: PlayerPlayedList? = array[1] as PlayerPlayedList?

            if (currentMusic == null || currentPlayedList == null) {
                null
            } else {
                UpdateData(
                    music = currentMusic,
                    isPlaying = currentPlayedList.state == PlayedListState.Playing,
                    cover = array[2] as ImageBitmap?,
                    isInFavorite = array[5] as Boolean,
                    playedListSize = (array[3] as Int).toLong(),
                    position = (array[4] as Int?)?.toLong() ?: 1L,
                )
            }
        }.distinctUntilChanged()

    private var isInit: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var startSeek: Int? = null

    init {
        player.registerListener(this)
        init()

        listenPlayerVolume()
        listenToState()
        playerListener()
        notificationListener()
        handleProgress()
    }

    private fun init() {
        workScope.launch {
            /*
            At launch, we will set the current played list (if any) state to be Loading,
            as we want to only load the played list, and not launch it immediately
             */
            playerRepository.setPlayedListState(PlayedListState.Loading)
            startSeek = playerRepository.getCurrentProgress().firstOrNull()
            isInit.value = true
        }
    }

    private fun listenPlayerVolume() {
        workScope.launch {
            settings.getFlowOn(SoulSearchingSettingsKeys.Player.PLAYER_VOLUME)
                .collectLatest { volume ->
                    player.setPlayerVolume(volume)
                }
        }
    }

    private fun launchWithInit(
        block: suspend () -> Unit,
    ) {
        workScope.launch {
            isInit.collectLatest { isInit ->
                if (isInit) {
                    block()
                }
            }
        }
    }

    private fun handleProgress() {
        workScope.launch {
            playerRepository.getCurrentMusic()
                .map { it?.playedListId }
                .distinctUntilChanged()
                .collectLatest { _ ->
                    println("PLAYBACK -- progress -- will update progress used")
                    while (true) {
                        delay(DELAY_BEFORE_SENDING_VALUE)
                        playerRepository.setProgress(player.getProgress())
                    }
                }
        }
    }

    private fun playerListener() {
        launchWithInit {
            playerRepository.getCurrentMusic()
                .map { it?.music?.path }
                .distinctUntilChanged()
                .collectLatest { currentMusicPath ->
                    println("PLAYBACK -- new music path to set: $currentMusicPath")
                    val currentMusic: Music? =
                        playerRepository.getCurrentMusic().firstOrNull()?.music
                    if (currentMusicPath == null || currentMusic == null) {
                        player.dismiss()
                    } else {
                        val currentState: PlayedListState? =
                            playerRepository.getCurrentState().firstOrNull()
                        when (currentState) {
                            PlayedListState.Playing -> {
                                player.setMusic(currentMusic)
                                player.launchMusic()
                            }

                            PlayedListState.Paused, PlayedListState.Loading -> {
                                player.setMusic(currentMusic)
                                // TODO PLAYER: what if the current music no longer exist at app launch? Maybe move to DB to listen to changes, in a proper table
                                player.onlyLoadMusic(
                                    seekTo = startSeek ?: 0,
                                )
                                startSeek = 0
                            }

                            PlayedListState.Cached -> {
                                player.dismiss()
                            }

                            else -> {
                                // no-op
                            }
                        }
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun notificationListener() {
        launchWithInit {
            notificationDataFlow.collectLatest { data ->
                if (data == null) {
                    notification.dismiss()
                } else {
                    notification.update(updateData = data)
                }
            }
        }
    }

    private fun updateNotification() {
        workScope.launch {
            val data: UpdateData = notificationDataFlow.firstOrNull() ?: return@launch
            notification.update(data)
        }
    }

    private fun listenToState() {
        launchWithInit {
            playerRepository.getCurrentState().distinctUntilChanged().collectLatest { state ->
                println("PLAYBACK -- listenToState -- state: $state")
                when (state) {
                    PlayedListState.Playing -> {
                        if (player.isPlaying() == false) {
                            player.play()
                        }
                    }

                    PlayedListState.Paused -> {
                        if (player.isPlaying() == true) {
                            player.pause()
                        }
                    }

                    PlayedListState.Cached, null -> {
                        player.dismiss()
                    }

                    else -> {
                        // no-op
                    }
                }
            }
        }
    }

    /**
     * Retrieves the current position in the current played song in milliseconds.
     */
    fun getMusicPosition(): Int =
        player.getProgress()

    /**
     * Stop the playback.
     * It will stop the service, dismiss the player and reset the player view model data.
     */
    suspend fun stopPlayback(resetPlayedList: Boolean = true) {
        if (resetPlayedList) {
            playerRepository.deleteCurrentPlayedList()
        } else {
            playerRepository.setPlayedListState(PlayedListState.Cached)
        }

        player.dismiss()
    }

    /**
     * Play or pause the player, depending on its current state.
     */
    suspend fun togglePlayPause() {
        playerRepository.togglePlayPause()
    }


    fun play() {
        workScope.launch {
            playerRepository.setPlayedListState(PlayedListState.Playing)
        }
    }

    fun pause() {
        workScope.launch {
            playerRepository.setPlayedListState(PlayedListState.Paused)
        }
    }

    /**
     * Seek to a given position in the current played music.
     */
    fun seekToPosition(position: Int) {
        player.seekToPosition(position)
        updateNotification()
    }

    fun handleKeyEvent(event: KeyEvent) {
        workScope.launch {
            val currentState: PlayedListState = playerRepository
                .getCurrentState()
                .firstOrNull() ?: return@launch

            if (event.isCtrlPressed && event.type == KeyEventType.KeyUp) {
                // If no music is being played, we do nothing
                if (currentState == PlayedListState.Cached) return@launch

                when (event.key) {
                    Key.P -> togglePlayPause()
                    Key.B, Key.DirectionLeft -> previous()
                    Key.N, Key.DirectionRight -> next()
                    else -> { /*no-op*/
                    }
                }
            }
        }
    }

    /**
     * Play the next song in queue.
     */
    suspend fun next() {
        val playerMode: PlayerMode = playerRepository.getCurrentMode().firstOrNull() ?: return

        if (playerMode == PlayerMode.Loop) {
            val currentMusicId: UUID =
                playerRepository.getCurrentMusic().firstOrNull()?.music?.musicId ?: return

            player.seekToPosition(0)
            launchMusicCount(currentMusicId)
        } else {
            playerRepository.playNext()
        }
    }

    /**
     * Play the previous song in queue.
     */
    suspend fun previous() {
        val playerMode: PlayerMode = playerRepository.getCurrentMode().firstOrNull() ?: return
        val shouldRewind =
            settings.get(SoulSearchingSettingsKeys.Player.IS_REWIND_ENABLED) && getMusicPosition() > REWIND_THRESHOLD

        if (shouldRewind || playerMode == PlayerMode.Loop) {
            val currentMusicId: UUID =
                playerRepository.getCurrentMusic().firstOrNull()?.music?.musicId ?: return

            player.seekToPosition(0)
            launchMusicCount(currentMusicId)
        } else {
            playerRepository.playPrevious()
        }
    }

    @Deprecated("No longer useful, use a state instead")
    suspend fun getNextMusic(): Music? =
        playerRepository.getNextMusic().firstOrNull()?.music

    @Deprecated("No longer useful, use a state instead")
    suspend fun getPreviousMusic(): Music? =
        playerRepository.getPreviousMusic().firstOrNull()?.music

    private suspend fun skipAndRemoveCurrentSong() {
        playerRepository.removeCurrentAndPlayNext()
    }

    suspend fun setAndPlayMusic(music: Music) {
        playerRepository.setCurrent(music.musicId)
        launchMusicCount(musicId = music.musicId)
    }

    private fun launchMusicCount(musicId: UUID) {
        updateMusicNbPlayedJob?.cancel()
        updateMusicNbPlayedJob = workScope.launch {
            delay(WAIT_TIME_BEFORE_UPDATE_NB_PLAYED)
            commonMusicUseCase.incrementNbPlayed(musicId = musicId)
        }
    }

    suspend fun switchPlayerMode() {
        playerRepository.switchPlayerMode()
    }

    suspend fun removeSongsFromPlayedPlaylist(musicIds: List<UUID>) {
        playerRepository.deleteAll(musicIds)
    }

    suspend fun addMusicToPlayNext(music: Music) {
        playerRepository.add(
            music = music,
            mode = AddMusicMode.Next,
        )
    }

    suspend fun addMusicToQueue(music: Music) {
        playerRepository.add(
            music = music,
            mode = AddMusicMode.Queue,
        )
    }

    // TODO PLAYER: Adapt with new system
    /**
     * Updates the played list after a reorder in it.
     */
    suspend fun updatePlayedListAfterReorder(
        newList: List<Music>
    ) {

    }

    suspend fun addMultipleMusicsToPlayNext(musics: List<Music>) {
        playerRepository.addAll(
            musics = musics,
            mode = AddMusicMode.Next,
        )
    }

    suspend fun addMultipleMusicsToQueue(musics: List<Music>) {
        playerRepository.addAll(
            musics = musics,
            mode = AddMusicMode.Queue,
        )
    }

    suspend fun isSameMusicAsCurrentPlayedOne(musicId: UUID): Boolean =
        playerRepository.getCurrentMusic().firstOrNull()?.music?.musicId == musicId

    // TODO PLAYER: Should be removed with new system?
    /**
     * Update the cover of the current played music.
     */
    fun updateCover(cover: ImageBitmap?) {

    }

    suspend fun playShuffle(musicList: List<Music>) {
        if (musicList.isEmpty()) return

        playerRepository.setup(
            playedListSetup = PlayedListSetup.fromSelection(
                musics = musicList.shuffled(),
                state = PlayedListState.Playing,
            ),
        )
    }

    suspend fun playSoulMix() {
        val totalByFolder: Int =
            settings.get(SoulSearchingSettingsKeys.Player.SOUL_MIX_TOTAL_BY_LIST)

        val musicList: List<Music> = commonMusicUseCase.getSoulMixMusics(totalByFolder)

        if (musicList.isEmpty()) return

        playerRepository.setup(
            playedListSetup = PlayedListSetup.fromSelection(
                musics = musicList,
                state = PlayedListState.Playing,
            ),
        )
    }

    suspend fun setCurrentPlaylistAndMusic(
        music: Music,
        musicList: List<Music>,
        playlistId: UUID?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ) {
        playerRepository.setup(
            playedListSetup = PlayedListSetup(
                musics = musicList,
                selectedMusic = music,
                listId = playlistId,
                isMain = isMainPlaylist,
                state = PlayedListState.Playing,
                forceOverride = isForcingNewPlaylist
            )
        )
    }

    /**************** PLAYER LISTENER ******************/

    override suspend fun onCompletion() {
        next()
    }

    override suspend fun onError() {
        skipAndRemoveCurrentSong()
    }


    companion object {
        private const val REWIND_THRESHOLD: Long = 5_000
        private const val WAIT_TIME_BEFORE_UPDATE_NB_PLAYED: Long = 3_000
        private const val DELAY_BEFORE_SENDING_VALUE: Long = 200L
    }
}