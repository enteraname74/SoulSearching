package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.lyrics.CommonLyricsUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

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
    commonPlaylistUseCase: CommonPlaylistUseCase,
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

    val state: StateFlow<PlayerViewState> = combine(
        playbackManager.state,
        commonPlaylistUseCase.getAllWithMusics(),
        playbackManager.playedList
    ) { playbackMainState, playlists, playedList ->
        when (playbackMainState) {
            is PlaybackManagerState.Data -> {
                PlayerViewState.Data(
                    currentMusic = playbackMainState.currentMusic,
                    currentMusicIndex = playbackMainState.currentMusicIndex,
                    isCurrentMusicInFavorite = playbackMainState.isCurrentMusicInFavorite,
                    playerMode = playbackMainState.playerMode,
                    isPlaying = playbackMainState.isPlaying,
                    playlistsWithMusics = playlists,
                    aroundSongs = listOfNotNull(
                        playbackMainState.previous,
                        playbackMainState.currentMusic,
                        playbackMainState.next,
                    ).distinctBy { it.musicId },
                    playedList = playedList,
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

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

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
            playbackManager.state.map {
                when (it) {
                    is PlaybackManagerState.Data -> it.currentState
                    PlaybackManagerState.Stopped -> null
                }
            }.collectLatest { state ->
                val isCollapsed = playerViewManager.currentValue == BottomSheetStates.COLLAPSED
                val canMaximize =
                    state == PlayedListState.Playing || state == PlayedListState.Paused
                val shouldMaximize = canMaximize && isCollapsed

                val shouldMinimize = state == PlayedListState.Loading && isCollapsed
                val shouldDismiss = state == PlayedListState.Cached || state == null

                when {
                    shouldDismiss -> playerViewManager.animateTo(BottomSheetStates.COLLAPSED)
                    shouldMaximize -> playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                    shouldMinimize -> playerViewManager.animateTo(BottomSheetStates.MINIMISED)
                }
            }
        }
        viewModelScope.launch {
            playbackManager.currentCover.collectLatest { cover ->
                colorThemeManager.setCurrentCover(cover = cover)
            }
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
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.switchPlayerMode()
        }
    }

    fun next() {
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.next()
        }
    }

    fun previous() {
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.previous()
        }
    }

    fun stopPlayback() {
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.stopPlayback(resetPlayedList = true)
        }
    }

    /**
     * Toggle the favorite status of the current music if there is one.
     */
    fun toggleFavoriteState() {
        (state.value as? PlayerViewState.Data)?.currentMusic?.let {
            CoroutineScope(Dispatchers.IO).launch {
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

    fun navigateToRemoteLyricsSettings() {
        _navigationState.value = PlayerNavigationState.ToRemoteLyricsSettings
    }

    fun showMusicBottomSheet(musicIds: List<UUID>) {
        _navigationState.value = PlayerNavigationState.ToMusicBottomSheet(musicIds)
    }
}