package com.github.enteraname74.soulsearching.features.playback.manager

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.player.AddMusicMode
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListSetup
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.player.PlayedListType
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayedListUser
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicInFavoritePlaylistUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.player.AddMusicsToSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.CreateSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.RegisterSharedPlayedListEventsListenerUseCase
import com.github.enteraname74.domain.usecase.player.RemoveMusicsFromSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.SyncPlayedListInformationUseCase
import com.github.enteraname74.domain.usecase.player.SyncPlayedListMusicsUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.features.playback.progressJob.PlaybackProgressJob
import com.github.enteraname74.soulsearching.features.playback.progressJob.PlaybackProgressJobCallbacks
import kotlinx.coroutines.CoroutineScope
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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO SHARED PLAYED LIST: How to properly indicate the current music progression if we are a guest?
@OptIn(ExperimentalUuidApi::class)
class PlaybackManager(
    private val playerRepository: PlayerRepository,
    private val settings: SoulSearchingSettings,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val createSharedPlayedListUseCase: CreateSharedPlayedListUseCase,
    private val addMusicsToSharedPlayedListUseCase: AddMusicsToSharedPlayedListUseCase,
    private val removeMusicsFromSharedPlayedListUseCase: RemoveMusicsFromSharedPlayedListUseCase,
    private val registerSharedPlayedListEventsListenerUseCase: RegisterSharedPlayedListEventsListenerUseCase,
    private val syncPlayedListInformationUseCase: SyncPlayedListInformationUseCase,
    private val syncPlayedListMusicsUseCase: SyncPlayedListMusicsUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    workDispatcher: WorkDispatcher,
) : KoinComponent, SoulSearchingPlayer.Listener {
    private val notification: SoulSearchingNotification by inject()
    private val player: SoulSearchingPlayer by inject()

    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private var updateMusicNbPlayedJob: Job? = null

    private val playbackProgressJob: PlaybackProgressJob = PlaybackProgressJob(
        playerRepository = playerRepository,
        workDispatcher = workDispatcher,
        callback = object : PlaybackProgressJobCallbacks {
            override suspend fun isPlaying(): Boolean =
                player.isPlaying() == true

            override suspend fun getMusicPosition(): Int =
                this@PlaybackManager.getMusicPosition()
        },
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentMusicFavoriteStatusState: Flow<Boolean> =
        playerRepository.getCurrentMusic().flatMapLatest { current ->
            current?.music?.musicId?.let {
                isMusicInFavoritePlaylistUseCase(musicId = it)
            } ?: flowOf(false)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSong: StateFlow<Music?> =
        playerRepository.getCurrentMusic().map {
            it?.music
        }.stateIn(
            scope = workScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val currentScope: Flow<PlayedListScope?> = playerRepository
        .getCurrentScope()

    val currentSongProgressionState: Flow<Int> = playbackProgressJob.state

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentCover: Flow<ImageBitmap?> =
        playerRepository.getCurrentMusic().map { currentMusic ->
            currentMusic?.music?.cover?.let { commonCoverUseCase.getCoverImageBitmap(it) }
        }

    // TODO PLAYER: Find a way to make drag and drop and paging list work together
    val playedList: Flow<List<Music>> = playerRepository.getAll()

    @Suppress("UNCHECKED_CAST")
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
            playerRepository.observeCurrentSharedUsers(),
            playerRepository.observeFullPlayerMusicUsers(),
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
                    currentScope = currentPlayedList.scope,
                    currentType = currentPlayedList.type,
                    users = array[8] as List<SharedPlayedListUser>,
                    playerMusicUsers = array[9] as List<FullPlayerMusicUser>
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
            playerRepository.getCurrentScope(),
        ) { array ->
            val currentMusic: Music? = (array[0] as PlayerMusic?)?.music
            val currentPlayedList: PlayerPlayedList? = array[1] as PlayerPlayedList?
            val playedListScope: PlayedListScope? = array[6] as PlayedListScope?

            if (currentMusic == null || currentPlayedList == null || playedListScope == null) {
                null
            } else {
                UpdateData(
                    music = currentMusic,
                    isPlaying = currentPlayedList.state == PlayedListState.Playing,
                    cover = array[2] as ImageBitmap?,
                    isInFavorite = array[5] as Boolean,
                    playedListSize = (array[3] as Int).toLong(),
                    position = (array[4] as Int?)?.toLong() ?: 1L,
                    playedListScope = playedListScope,
                )
            }
        }.distinctUntilChanged()

    private var isInit: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var startSeek: Int? = null

    init {
        workScope.launch {
            player.registerListener(this@PlaybackManager)
        }
        init()

        listenPlayerVolume()
        listenToMusicCount()
        listenToState()
        playerListener()
        notificationListener()
        sharedListCurrentMusicUpdateListener()
        noSharedPlayedListListener()
    }

    private fun init() {
        workScope.launch {
            // TODO PLAYER: Maybe not needed anymore with new draggable view management.
            /*
            At launch, we will set the current played list (if any) state to be Loading,
            as we want to only load the played list, and not launch it immediately
             */
            playerRepository.setPlayedListState(PlayedListState.Loading)
            startSeek = playerRepository.getCurrentProgress().firstOrNull()

            val remoteList = playerRepository.getCurrentPlayedList().firstOrNull()
                ?.takeIf { it.type is PlayedListType.Shared }

            /*
            If we are in a remote played list, we MUST wait for the remote list to be synced,
            before letting the user do anything on the remote list if he is the admin,
            like setting the current played song.
             */
            if (remoteList != null) {
                registerSharedPlayedListEventsListenerUseCase(
                    listId = remoteList.id,
                    onConnected = {
                        // Fetch the latest data to be sure that on, app launch, we are up-to-date with the backend.
                        syncPlayedListInformationUseCase()
                        syncPlayedListMusicsUseCase()
                        isInit.value = true
                    }
                )
            } else {
                isInit.value = true
            }
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

    private fun listenToMusicCount() {
        launchWithInit {
            currentSong
                .map { it?.musicId }
                .distinctUntilChanged()
                /*
                We drop the first value of the flow to avoid incrementing the music total playing number
                when we relaunch the app.
                There could we a case where we quit the app before the increment was done.
                Thus, relaunching the app would never increment the current music.
                But this will do for now.
                 */
                .drop(1)
                .collectLatest { currentMusicId ->
                    if (currentMusicId != null) {
                        launchMusicCount(currentMusicId)
                    } else {
                        updateMusicNbPlayedJob?.cancel()
                    }
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

    private fun playerListener() {
        launchWithInit {
            combine(
                playerRepository.getCurrentMusic().map { it?.music?.path }.distinctUntilChanged(),
                playerRepository.getCurrentScope().distinctUntilChanged(),
            ) { musicPath, playerScope ->
                Pair(musicPath, playerScope)
            }.collectLatest { data ->
                val currentMusicPath = data.first
                val playerScope = data.second

                val currentMusic: Music? =
                    playerRepository.getCurrentMusic().firstOrNull()?.music
                if (currentMusicPath == null || currentMusic == null) {
                    player.dismiss()
                } else {
                    val currentState: PlayedListState? =
                        playerRepository.getCurrentState().firstOrNull()

                    when (currentState) {
                        PlayedListState.Playing if playerScope?.isAdmin == true -> {
                            player.setMusic(currentMusic)
                            player.play()
                        }

                        // We lost the admin status, and we were playing a song, we must stop the playback
                        PlayedListState.Playing if playerScope?.isAdmin == false -> {
                            player.pause()
                        }

                        PlayedListState.Paused, PlayedListState.Loading -> {
                            if (playerScope?.isAdmin == true) {
                                player.setMusic(currentMusic)
                                player.seekToPosition(startSeek ?: 0)
                                startSeek = 0
                                playbackProgressJob.launchDurationJobIfNecessary()
                            }
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

    private fun noSharedPlayedListListener() {
        launchWithInit {
            playerRepository
                .getCurrentPlayedList()
                .map { it?.type is PlayedListType.Shared }
                .distinctUntilChanged()
                .collect { isShared ->
                    if (!isShared) {
                        playerRepository.removeSharedPlayedListEventsListener()
                        deleteMusicUseCase.deleteSharedMusics()
                    }
                }
        }
    }

    private fun sharedListCurrentMusicUpdateListener() {
        launchWithInit {
            playerRepository
                .getCurrentPlayedList()
                .map { it?.scope == PlayedListScope.SharedHost }
                .distinctUntilChanged()
                .collectLatest { isOwner ->
                    if (isOwner) {
                        playerRepository
                            .getCurrentMusic()
                            .mapNotNull { it?.music?.remoteId }
                            .distinctUntilChanged()
                            .collectLatest { currentMusicRemoteId ->
                                runCatching {
                                    playerRepository.updateCurrentRemoteMusic(currentMusicRemoteId)
                                }
                            }
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
            combine(
                playerRepository.getCurrentState().distinctUntilChanged(),
                playerRepository.getCurrentScope().distinctUntilChanged()
            ) { state, scope ->
                Pair(state, scope)
            }.collectLatest { data ->
                val state = data.first
                val scope = data.second

                when (state) {
                    PlayedListState.Playing if scope?.isAdmin == true -> {
                        playbackProgressJob.launchDurationJobIfNecessary()
                        if (player.isPlaying() == false) {
                            player.play()
                        }
                    }

                    PlayedListState.Paused if scope?.isAdmin == true -> {
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

    fun getCachedPlaylist(playlistId: String): Flow<PlayedListToContinue?> =
        playerRepository.getCachedPlayedList(playlistId)

    suspend fun continuePlayedList(playedListId: Uuid): SoulResult<Unit> = SoulResult.runCatching {
        playerRepository.continuePlayedList(playedListId)
    }

    suspend fun deletePlayedList(playedListId: Uuid) {
        playerRepository.deletePlayedList(playedListId)
    }

    /**
     * Retrieves the current position in the current played song in milliseconds.
     */
    suspend fun getMusicPosition(): Int =
        player.getProgress()

    /**
     * Stop the playback.
     * It will stop the service, dismiss the player and reset the player view model data.
     */
    suspend fun stopPlayback(resetPlayedList: Boolean = true) {
        playbackProgressJob.releaseDurationJob()
        if (resetPlayedList) {
            playerRepository.deleteCurrentPlayedList()
        }

        player.dismiss()
        notification.dismiss()
    }

    /**
     * Play or pause the player, depending on its current state.
     */
    suspend fun togglePlayPause() {
        withAdminRight {
            playerRepository.togglePlayPause()
        }
    }

    fun play() {
        workScope.launch {
            withAdminRight {
                playerRepository.setPlayedListState(PlayedListState.Playing)
            }
        }
    }

    fun pause() {
        workScope.launch {
            withAdminRight {
                playerRepository.setPlayedListState(PlayedListState.Paused)
            }
        }
    }

    /**
     * Seek to a given position in the current played music.
     */
    suspend fun seekTo(millis: Int) {
        withAdminRight {
            player.seekToPosition(millis)
            updateNotification()
            playbackProgressJob.launchDurationJobIfNecessary()
        }
    }

    /**
     * Seek forward in the current song.
     * If the seek will go out of the bound of the current song, skip to the next one.
     */
    suspend fun seekForward() {
        withAdminRight {
            val currentPosMillis = player.getProgress()
            val currentMusicDuration = player.getMusicDuration().takeIf { it > 0 } ?: return@withAdminRight
            val newPosMillis = currentPosMillis + INNER_SEEK_MILLIS

            if (newPosMillis >= currentMusicDuration) {
                next()
            } else {
                player.seekToPosition(newPosMillis)
            }
        }
    }

    /**
     * Seek backward in the current song.
     * If the seek will go out of the bound of the song, play the previous one.
     */
    suspend fun seekBackward() {
        withAdminRight {
            val currentPosMillis = player.getProgress()
            val newPosMillis = currentPosMillis - INNER_SEEK_MILLIS

            if (newPosMillis < 0) {
                previous()
            } else {
                player.seekToPosition(newPosMillis)
            }
        }
    }

    /**
     * Up the volume by a step (0.1) if possible
     */
    fun volumeUp() {
        val currentVolume = settings.get(SoulSearchingSettingsKeys.Player.PLAYER_VOLUME)
        val newVolume = min(currentVolume + 0.1f, 1f)
        settings.set(
            key = SoulSearchingSettingsKeys.Player.PLAYER_VOLUME.key,
            value = newVolume,
        )
    }

    /**
     * Lower the volume by a step (0.1) if possible
     */
    fun volumeDown() {
        val currentVolume = settings.get(SoulSearchingSettingsKeys.Player.PLAYER_VOLUME)
        val newVolume = max(currentVolume - 0.1f, 0f)
        settings.set(
            key = SoulSearchingSettingsKeys.Player.PLAYER_VOLUME.key,
            value = newVolume,
        )
    }

    suspend fun toggleFavorite() {
        val currentMusic: Music = currentSong.value?.takeIf { it.scope != Scope.SharedPlayedList } ?: return
        toggleMusicFavoriteStatusUseCase(musicId = currentMusic.musicId)
    }

    fun handleKeyboardAction(action: KeyboardAction) {
        workScope.launch {
            // Block if no played list is available
            playerRepository
                .getCurrentState()
                .firstOrNull()
                ?.takeIf { it != PlayedListState.Cached } ?: return@launch

            // Block if we cannot control the played list
            playerRepository
                .getCurrentScope()
                .firstOrNull()
                ?.takeIf { it.isAdmin } ?: return@launch

            when (action) {
                KeyboardAction.TogglePlayPause -> togglePlayPause()
                KeyboardAction.Previous -> previous()
                KeyboardAction.Next -> next()
                KeyboardAction.SeekForward -> seekForward()
                KeyboardAction.SeekBackward -> seekBackward()
                KeyboardAction.VolumeUp -> volumeUp()
                KeyboardAction.VolumeDown -> volumeDown()
                KeyboardAction.ToggleFavorite -> toggleFavorite()
            }
        }
    }

    /**
     * Play the next song in queue.
     */
    suspend fun next() {
        withAdminRight {
            val playerMode: PlayerMode = playerRepository.getCurrentMode().firstOrNull() ?: return@withAdminRight
            val size: Int = playerRepository.getSize().firstOrNull() ?: return@withAdminRight

            if (playerMode == PlayerMode.Loop || size == 1) {
                val currentMusic: Music =
                    playerRepository.getCurrentMusic().firstOrNull()?.music ?: return@withAdminRight

                player.setMusic(currentMusic)
                player.play()
                playerRepository.setPlayedListState(PlayedListState.Playing)
                launchMusicCount(currentMusic.musicId)
            } else {
                playerRepository.playNext()
            }
        }
    }

    /**
     * Play the previous song in queue.
     */
    suspend fun previous(skipRewind: Boolean = false) {
        withAdminRight {
            val playerMode: PlayerMode = playerRepository.getCurrentMode().firstOrNull() ?: return@withAdminRight
            val size: Int = playerRepository.getSize().firstOrNull() ?: return@withAdminRight
            val shouldRewind =
                settings.get(SoulSearchingSettingsKeys.Player.IS_REWIND_ENABLED) && getMusicPosition() > REWIND_THRESHOLD && !skipRewind

            if (shouldRewind || playerMode == PlayerMode.Loop || size == 1) {
                val currentMusicId: Uuid =
                    playerRepository.getCurrentMusic().firstOrNull()?.music?.musicId ?: return@withAdminRight

                player.seekToPosition(0)
                updateNotification()
                playerRepository.setPlayedListState(PlayedListState.Playing)
                launchMusicCount(currentMusicId)
            } else {
                playerRepository.playPrevious()
            }
        }
    }

    /**
     * When we cannot play the current music,
     * we will manage the error differently depending on the music source.
     *
     * If the music is local, we will skip the music and play the next one.
     * If the music is remote, we will pause the music.
     */
    private suspend fun onCurrentMusicError() {
        val currentMusic: Music = playerRepository.getCurrentMusic().firstOrNull()?.music ?: return
        if (currentMusic.isRemoteOnly) {
            playerRepository.setPlayedListState(PlayedListState.Paused)
        } else {
            playerRepository.removeCurrentAndPlayNext()
        }
    }

    suspend fun setAndPlayMusicFromCurrentPlayedList(music: Music) {
        playerRepository.setCurrent(music.musicId)
        playerRepository.setPlayedListState(PlayedListState.Playing)
    }

    private fun launchMusicCount(musicId: Uuid) {
        updateMusicNbPlayedJob?.cancel()
        updateMusicNbPlayedJob = workScope.launch {
            delay(WAIT_TIME_BEFORE_UPDATE_NB_PLAYED.milliseconds)
            commonMusicUseCase.incrementNbPlayed(musicId = musicId)
        }
    }

    suspend fun switchPlayerMode() {
        playerRepository.switchPlayerMode()
    }

    suspend fun removeSongsFromPlayedList(musicIds: List<Uuid>): SoulResult<Unit> {
        val scope =
            playerRepository.getCurrentScope().firstOrNull()

        return if (scope?.isRemote == true) {
            removeMusicsFromSharedPlayedListUseCase(musicIds)
        } else {
            playerRepository.deleteAll(musicIds)
            SoulResult.ofSuccess()
        }
    }

    /**
     * Updates the played list after a reorder in it.
     */
    suspend fun moveMusic(
        fromMusicId: Uuid,
        afterMusicId: Uuid
    ) {
        playerRepository.moveMusic(
            fromMusicId = fromMusicId,
            afterMusicId = afterMusicId,
        )
    }

    suspend fun addMultipleMusicsToPlayNext(musics: List<Music>): SoulResult<Unit> {
        val scope =
            playerRepository.getCurrentScope().firstOrNull()
        return if (scope?.isRemote == true) {
            addMusicsToSharedPlayedListUseCase.local(musicIds = musics.map { it.musicId })
        } else {
            playerRepository.addAll(
                musics = musics,
                mode = AddMusicMode.Next,
            )
            SoulResult.ofSuccess()
        }
    }

    suspend fun addMultipleMusicsToQueue(musics: List<Music>): SoulResult<Unit> {
        val scope =
            playerRepository.getCurrentScope().firstOrNull()
        return if (scope?.isRemote == true) {
            addMusicsToSharedPlayedListUseCase.local(musicIds = musics.map { it.musicId })
        } else {
            playerRepository.addAll(
                musics = musics,
                mode = AddMusicMode.Queue,
            )
            SoulResult.ofSuccess()
        }
    }

    suspend fun playShuffle(
        musicList: List<Music>,
        playlistId: String?,
        isMain: Boolean,
    ): SoulResult<Boolean> = SoulResult.runCatching {
        playerRepository.setup(
            playedListSetup = PlayedListSetup.fromSelection(
                musics = musicList.shuffled(),
                state = PlayedListState.Playing,
                playlistId = playlistId,
                isMain = isMain,
                type = PlayedListType.Local,
                scope = PlayedListScope.LocalUser,
            ),
        )
    }

    suspend fun playSoulMix(): SoulResult<Boolean> = SoulResult.runCatching {
        val totalByFolder: Int =
            settings.get(SoulSearchingSettingsKeys.Player.SOUL_MIX_TOTAL_BY_LIST)

        val musicList: List<Music> = commonMusicUseCase.getSoulMixMusics(totalByFolder)

        playerRepository.setup(
            playedListSetup = PlayedListSetup.fromSelection(
                musics = musicList,
                state = PlayedListState.Playing,
                isMain = false,
                playlistId = null,
                type = PlayedListType.Local,
                scope = PlayedListScope.LocalUser,
            ),
        )
    }

    suspend fun setCurrentPlaylistAndMusic(
        music: Music,
        musicList: List<Music>,
        playlistId: String?,
        isMainPlaylist: Boolean = false,
        isForcingNewPlaylist: Boolean = false
    ): SoulResult<Boolean> = SoulResult.runCatching {
        playerRepository.setup(
            playedListSetup = PlayedListSetup(
                musics = musicList,
                selectedMusic = music,
                listId = playlistId,
                isMain = isMainPlaylist,
                state = PlayedListState.Playing,
                type = PlayedListType.Local,
                forceOverride = isForcingNewPlaylist,
                scope = PlayedListScope.LocalUser,
            )
        )
    }

    suspend fun startSharedList(
        musicIds: List<Uuid>
    ): SoulResult<Unit> =
        createSharedPlayedListUseCase(
            musicIds = musicIds,
        )

    suspend fun removeUserFromSharedList(
        userId: Uuid,
        deviceId: String
    ): SoulResult<Unit> = SoulResult.runCatching {
        playerRepository.removeUser(
            userId = userId,
            deviceId = deviceId,
        )
    }

    suspend fun getDeviceId(): String =
        playerRepository.getDeviceId()

    private suspend fun withAdminRight(
        block: suspend () -> Unit
    ) {
        if (playerRepository.isAdminOfPlayedList()) {
            block()
        }
    }

    /**************** PLAYER LISTENER ******************/

    override suspend fun onCompletion() {
        next()
    }

    override suspend fun onError() {
        onCurrentMusicError()
    }

    override suspend fun onPause() {
        pause()
    }

    override suspend fun onPlay() {
        play()
    }

    companion object {
        private const val REWIND_THRESHOLD: Long = 5_000
        private const val WAIT_TIME_BEFORE_UPDATE_NB_PLAYED: Long = 3_000

        private const val INNER_SEEK_MILLIS: Int = 5_000
    }

    enum class KeyboardAction {
        TogglePlayPause,
        Previous,
        Next,
        SeekForward,
        SeekBackward,
        VolumeUp,
        VolumeDown,
        ToggleFavorite,
    }
}
