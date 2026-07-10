package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListType
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.usecase.lyrics.CommonLyricsUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.player.AddMusicsToSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.state.SharedListState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.dialog.AddUrlToSharedPlayedListDialog
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.dialog.RemoveUserFromPlayedListDialog
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/**
 * Handler for managing the PlayerViewModel.
 */
class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    settings: SoulSearchingSettings,
    private val colorThemeManager: ColorThemeManager,
    private val commonLyricsUseCase: CommonLyricsUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    val multiSelectionManager: MultiSelectionManager,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val addMusicsToSharedPlayedListUseCase: AddMusicsToSharedPlayedListUseCase,
    private val savedStateHandle: SavedStateHandle,
    commonUserUseCase: CommonUserUseCase,
    private val workDispatcher: WorkDispatcher,
) : ViewModel() {

    val multiSelectionState: StateFlow<MultiSelectionState> = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    val currentSongProgressionState: StateFlow<Int> = playbackManager.currentSongProgressionState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val lyricsState: StateFlow<LyricsFetchState> = settings.getFlowOn(
        SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED
    ).flatMapLatest { isRemoteFetchEnabled ->
        playbackManager.currentSong.flatMapLatest { music ->
            channelFlow {
                if (music == null) {
                    send(LyricsFetchState.NoLyricsFound)
                } else {
                    send(LyricsFetchState.FetchingLyrics)

                    var lyrics: MusicLyrics? = commonLyricsUseCase.getLocalLyricsForMusic(music = music)
                    if (lyrics == null && isRemoteFetchEnabled) {
                        lyrics = commonLyricsUseCase.getRemoteLyricsForMusic(music = music)
                    }

                    if (lyrics == null) {
                        if (isRemoteFetchEnabled) {
                            send(LyricsFetchState.NoLyricsFound)
                        } else {
                            send(LyricsFetchState.NoPermission)
                        }
                    } else {
                        currentSongProgressionState.collectLatest { progression ->
                            send(
                                LyricsFetchState.FoundLyrics(
                                    lyrics = lyrics,
                                    currentMusicId = music.musicId,
                                    highlightedLyricsLine = lyrics
                                        .syncedLyrics
                                        ?.indexOfLast { it.timestampMs < progression }
                                        ?.takeIf { it >= 0 }
                                )
                            )
                        }
                    }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LyricsFetchState.FetchingLyrics
    )

    val viewSettingsState: StateFlow<PlayerViewSettingsState> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED),
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN),
    ) { canSwipeCover, isMinimisedSongProgressionShown ->
        PlayerViewSettingsState(
            canSwipeCover = canSwipeCover,
            isMinimisedSongProgressionShown = isMinimisedSongProgressionShown,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PlayerViewSettingsState(
            canSwipeCover = SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED.defaultValue,
            isMinimisedSongProgressionShown = SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN.defaultValue,
        )
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)

    val state: StateFlow<PlayerViewState> = combine(
        playbackManager.state,
        playbackManager.playedList,
        _dialogState,
        commonUserUseCase.observeUser(),
    ) { playbackMainState, playedList, dialog, user ->
        when (playbackMainState) {
            is PlaybackManagerState.Data -> {
                PlayerViewState.Data(
                    currentMusic = playbackMainState.currentMusic,
                    currentMusicIndex = playbackMainState.currentMusicIndex,
                    isCurrentMusicInFavorite = playbackMainState.isCurrentMusicInFavorite,
                    playerMode = playbackMainState.playerMode,
                    playbackCommandsState = buildPlaybackState(
                        scope = playbackMainState.currentScope,
                        isPlaying = playbackMainState.isPlaying,
                        currentMusicScope = playbackMainState.currentMusic.scope
                    ),
                    aroundSongs = if (playbackMainState.playerMode == PlayerMode.Loop) {
                        listOfNotNull(
                            playbackMainState.currentMusic,
                        )
                    } else {
                        listOfNotNull(
                            playbackMainState.previous,
                            playbackMainState.currentMusic,
                            playbackMainState.next,
                        ).run {
                            // UI fix to avoid clipping when swiping cover on player view.
                            if (playbackMainState.currentMusic == playbackMainState.next) {
                                drop(1)
                            } else {
                                this
                            }
                        }
                    },
                    playedList = playedList,
                    playedListScope = playbackMainState.currentScope,
                    sharedListState = buildSharedListState(
                        playbackManagerState = playbackMainState,
                        currentUser = user,
                    ),
                    playerMusicUsers = playbackMainState.playerMusicUsers,
                    dialog = dialog,
                )
            }

            PlaybackManagerState.Stopped -> {
                colorThemeManager.setCurrentCover(cover = null)
                PlayerViewState.Closed
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PlayerViewState.Closed
    )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _navigationState: MutableStateFlow<PlayerNavigationState> = MutableStateFlow(
        PlayerNavigationState.Idle,
    )
    val navigationState: StateFlow<PlayerNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        multiSelectionManager.clearMultiSelection()
        _navigationState.value = PlayerNavigationState.Idle
    }

    init {
        viewModelScope.launch {
            playbackManager.state.collect { playbackState ->
                val isCollapsed = playerViewManager.currentValue == BottomSheetStates.COLLAPSED
                val hasRestoredPlayerView = savedStateHandle.get<Boolean>(PlayerViewInitKey) ?: false
                println("CLUELESS -- IS COLLAPSED: $isCollapsed, HAS RESTORED: $hasRestoredPlayerView")
                if (playerViewManager.isAnimationRunning) return@collect
                when (playbackState) {
                    /*
                    If playback is stopped, we must ensure that the view is collapsed.
                     */
                    PlaybackManagerState.Stopped -> {
                        playerViewManager.animateTo(BottomSheetStates.COLLAPSED)
                    }
                    /*
                    On first app launch, if we had a played list,
                    we need to move to minimized mode.
                     */
                    is PlaybackManagerState.Data if !hasRestoredPlayerView && isCollapsed -> {
                        println("CLUELESS -- THERE")
                        playerViewManager.animateTo(BottomSheetStates.MINIMISED)
                    }
                    /*
                    If we have a played list,
                    and we are in a loading state,
                    we should animate to minimized mode if the view is collapsed.
                     */
                    is PlaybackManagerState.Data if isCollapsed && playbackState.currentState == PlayedListState.Loading -> {
                        playerViewManager.animateTo(BottomSheetStates.MINIMISED)
                    }
                    /*
                    Finally, for other cases were the view is collapsed and we have a played list,
                    animate to expanded.
                     */
                    is PlaybackManagerState.Data if isCollapsed -> {
                        println("CLUELESS -- HERE")
                        playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                    }
                    else -> {
                        // no-op
                    }
                }
                savedStateHandle[PlayerViewInitKey] = true
            }
        }
        viewModelScope.launch {
            playbackManager.currentCover.collectLatest { cover ->
                colorThemeManager.setCurrentCover(cover = cover)
            }
        }
    }

    private fun buildPlaybackState(
        scope: PlayedListScope,
        isPlaying: Boolean,
        currentMusicScope: Scope,
    ): PlaybackCommandsState =
        PlaybackCommandsState(
            isPlaying = isPlaying,
            previous = { previous() }.takeIf { scope.isAdmin },
            next = { next() }.takeIf { scope.isAdmin },
            togglePlayPause = { togglePlayPause() }.takeIf { scope.isAdmin },
            changePlayerMode = { changePlayerMode() }.takeIf { !scope.isRemote },
            toggleFavoriteState = { toggleFavoriteState() }.takeIf { currentMusicScope != Scope.SharedPlayedList },
            seekTo = { newPosition: Int -> seekTo(newPosition) }.takeIf { scope.isAdmin }
        )

    private suspend fun buildSharedListState(
        playbackManagerState: PlaybackManagerState.Data,
        currentUser: User?,
    ): SharedListState? {
        val notRemote = !playbackManagerState.currentScope.isRemote
        val isAdmin = playbackManagerState.currentScope.isAdmin
        val deviceId = playbackManager.getDeviceId()
        val code: String? = (playbackManagerState.currentType as? PlayedListType.Shared)?.invitationCode

        return if (notRemote || code == null) {
            null
        } else {
            val host = playbackManagerState.users.find { it.isOwner }?.let { owner ->
                val userDuplicates = playbackManagerState.users.filter { it.username == owner.username }
                val index = userDuplicates
                    .indexOfFirst { it.listUserId == owner.listUserId }
                    .takeIf { it >= 0 && userDuplicates.size > 1 }

                SharedListState.User(
                    id = owner.id,
                    deviceId = owner.deviceId,
                    username = owner.username,
                    appearance = index,
                    status = owner.status,
                    isCurrentUser = owner.id == currentUser?.id
                        && owner.deviceId == deviceId,
                    onRemove = null,
                )
            }

            val guests = playbackManagerState
                .users
                .groupBy { it.username }
                .flatMap { (_, duplicates) ->
                    duplicates.mapIndexedNotNull { index, user ->
                        val isCurrentUser = user.id == currentUser?.id
                            && user.deviceId == deviceId

                        SharedListState.User(
                            id = user.id,
                            username = user.username,
                            deviceId = user.deviceId,
                            appearance = index.takeIf { duplicates.size > 1 },
                            status = user.status,
                            isCurrentUser = isCurrentUser,
                            onRemove = {
                                showRemoveUserDialog(
                                    userId = user.id,
                                    deviceId = user.deviceId,
                                )
                            }.takeIf { isAdmin && !isCurrentUser }
                        ).takeIf { !user.isOwner }
                    }
                }

            SharedListState(
                code = code,
                host = host,
                guests = guests,
            )
        }
    }

    /**
     * Set the current music position.
     */
    fun seekTo(position: Int) {
        viewModelScope.launch {
            playbackManager.seekToPosition(position = position)
        }
    }

    /**
     * Set the playing state.
     */
    fun togglePlayPause() {
        viewModelScope.launch {
            playbackManager.togglePlayPause()
        }
    }

    /**
     * Set the player mode.
     */
    fun changePlayerMode() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            playbackManager.switchPlayerMode()
        }
    }

    fun next() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            playbackManager.next()
        }
    }

    fun previous() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            playbackManager.previous()
        }
    }

    fun stopPlayback() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            playbackManager.stopPlayback(resetPlayedList = true)
        }
    }

    /**
     * Toggle the favorite status of the current music if there is one.
     */
    fun toggleFavoriteState() {
        (state.value as? PlayerViewState.Data)?.currentMusic?.let {
            CoroutineScope(workDispatcher.dispatcher).launch {
                toggleMusicFavoriteStatusUseCase(musicId = it.musicId)
            }
        }
    }

    fun navigateToArtist(selectedArtist: Artist) {
        _navigationState.value = PlayerNavigationState.ToArtist(
            artistId = selectedArtist.artistId,
        )
    }

    fun navigateToAlbum() {
        (state.value as? PlayerViewState.Data)?.currentMusic?.let { currentMusic ->
            viewModelScope.launch {
                _navigationState.value = PlayerNavigationState.ToAlbum(
                    albumId = currentMusic.album.albumId,
                )
            }
        }
    }

    fun onSwipeMusic(music: Music) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            val result = playbackManager.removeSongsFromPlayedList(
                musicIds = listOf(music.musicId),
            )
            feedbackPopUpManager.showErrorIfAny(result)
        }
    }

    fun onClickOnMusic(music: Music) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            playbackManager.setAndPlayMusicFromCurrentPlayedList(music)
        }
    }

    fun onAddFromUrlClicked() {
        _dialogState.value = AddUrlToSharedPlayedListDialog(
            onConfirm = ::addFromUrl,
            onDismiss = { _dialogState.value = null }
        )
    }

    private fun addFromUrl(url: String) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            when (val result = addMusicsToSharedPlayedListUseCase.url(url)) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> _dialogState.value = null
            }
        }
    }

    private fun showRemoveUserDialog(
        userId: Uuid,
        deviceId: String
    ) {
        _dialogState.value = RemoveUserFromPlayedListDialog(
            onRemove = {
                removeUser(
                    userId = userId,
                    deviceId = deviceId,
                )
            },
            onClose = { _dialogState.value = null }
        )
    }

    private fun removeUser(
        userId: Uuid,
        deviceId: String
    ) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(
                playbackManager.removeUserFromSharedList(
                    userId = userId,
                    deviceId = deviceId,
                )
            )
            _dialogState.value = null
        }
    }

    fun navigateToRemoteLyricsSettings() {
        _navigationState.value = PlayerNavigationState.ToRemoteLyricsSettings
    }

    fun showMusicBottomSheet(musicIds: List<Uuid>) {
        _navigationState.value = PlayerNavigationState.ToMusicBottomSheet(musicIds)
    }

    private companion object {
        const val PlayerViewInitKey: String = "PlayerViewInitKey"
    }
}
