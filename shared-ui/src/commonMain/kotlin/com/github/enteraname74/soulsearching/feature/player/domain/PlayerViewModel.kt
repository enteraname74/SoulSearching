package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.lyrics.CommonLyricsUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicInFavoritePlaylistUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.musicalbum.GetAlbumIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Handler for managing the PlayerViewModel.
 */
class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    settings: SoulSearchingSettings,
    private val colorThemeManager: ColorThemeManager,
    private val commonLyricsUseCase: CommonLyricsUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val getAlbumIdFromMusicIdUseCase: GetAlbumIdFromMusicIdUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    private val commonMusicUseCase: CommonMusicUseCase,
    commonPlaylistUseCase: CommonPlaylistUseCase,
) : ScreenModel,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    MultiMusicBottomSheetDelegate by multiMusicBottomSheetDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {


    val multiSelectionState: StateFlow<MultiSelectionState> = multiSelectionManagerImpl.state
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentMusicFavoriteStatusState: Flow<Boolean> =
        playbackManager.mainState.flatMapLatest { playbackState ->
            when (playbackState) {
                is PlaybackManagerState.Data -> {
                    isMusicInFavoritePlaylistUseCase(
                        musicId = playbackState.currentMusic.musicId,
                    )
                }

                PlaybackManagerState.Stopped -> {
                    flowOf(false)
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentMusicArtists: Flow<List<Artist>> =
        playbackManager.mainState.flatMapLatest { playbackState ->
            when (playbackState) {
                is PlaybackManagerState.Data -> {
                    commonArtistUseCase.getArtistsOfMusic(
                        music = playbackState.currentMusic,
                    )
                }
                PlaybackManagerState.Stopped -> {
                    flowOf(emptyList())
                }
            }
        }

    val currentSongProgressionState = playbackManager.currentSongProgressionState

    @OptIn(ExperimentalCoroutinesApi::class)
    val lyricsState: StateFlow<LyricsFetchState> = settings.getFlowOn(
        SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED
    ).flatMapLatest { isRemoteFetchEnabled ->
        if (isRemoteFetchEnabled) {
            playbackManager.currentSong.flatMapLatest { music ->
                if (music == null) {
                    flowOf(LyricsFetchState.FetchingLyrics)
                } else {
                    val lyrics: MusicLyrics? = commonLyricsUseCase.getLyricsForMusic(music = music)
                    if (lyrics == null) {
                        flowOf(LyricsFetchState.NoLyricsFound)
                    } else {
                        currentSongProgressionState.mapLatest { progression ->
                            LyricsFetchState.FoundLyrics(
                                lyrics = lyrics,
                                currentMusicId = music.musicId,
                                highlightedLyricsLine = lyrics
                                    .syncedLyrics
                                    ?.indexOfLast { it.timestampMs < progression }
                                    ?.takeIf { it >= 0 }
                            )
                        }
                    }
                }
            }
        } else {
            flowOf(LyricsFetchState.NoPermission)
        }
    }.stateIn(
        scope = screenModelScope,
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
        screenModelScope,
        SharingStarted.Eagerly,
        PlayerViewSettingsState(
            canSwipeCover = SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED.defaultValue,
            isMinimisedSongProgressionShown = SoulSearchingSettingsKeys.Player.IS_MINIMISED_SONG_PROGRESSION_SHOWN.defaultValue,
        )
    )

    val state: StateFlow<PlayerViewState> = combine(
        playbackManager.mainState,
        commonPlaylistUseCase.getAllWithMusics(),
        currentMusicFavoriteStatusState,
        currentMusicArtists
    ) { playbackMainState, playlists, isCurrentMusicInFavorite, currentMusicArtists ->
        when (playbackMainState) {
            is PlaybackManagerState.Data -> {
                PlayerViewState.Data(
                    currentMusic = playbackMainState.currentMusic,
                    artistsOfCurrentMusic = currentMusicArtists,
                    currentMusicIndex = playbackMainState.currentMusicIndex,
                    isCurrentMusicInFavorite = isCurrentMusicInFavorite,
                    playedList = playbackMainState.playedList,
                    playerMode = playbackMainState.playerMode,
                    isPlaying = playbackMainState.isPlaying,
                    playlistsWithMusics = playlists,
                    aroundSongs = getAroundSongs(playbackMainState.currentMusic),
                    initPlayerWithMinimiseView = playbackMainState.minimisePlayer,
                )
            }

            PlaybackManagerState.Stopped -> {
                colorThemeManager.setCurrentCover(cover = null)
                PlayerViewState.Closed
            }
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.Eagerly,
        PlayerViewState.Closed
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<PlayerNavigationState> = MutableStateFlow(
        PlayerNavigationState.Idle,
    )
    val navigationState: StateFlow<PlayerNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        multiSelectionManagerImpl.clearMultiSelection()
        _navigationState.value = PlayerNavigationState.Idle
    }

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = PlayerNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = ::getPlaylistsWithMusics,
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )
    }

    private fun getPlaylistsWithMusics(): List<PlaylistWithMusics> =
        (state.value as? PlayerViewState.Data)?.playlistsWithMusics ?: emptyList()

    /**
     * Retrieve a list containing the current song and its around songs (previous and next).
     * If no songs are played, return a list containing null. If the played list contains only
     * the current song, it will return a list with only the current song.
     */
    private suspend fun getAroundSongs(
        currentSong: Music,
    ): List<Music?> {
        val playerState = (state.value as? PlayerViewState.Data) ?: return emptyList()
        if (!viewSettingsState.value.canSwipeCover) return emptyList()

        val currentSongIndex = playerState.currentMusicIndex

        if (currentSongIndex == -1) return emptyList()

        if (playerState.playedList.size == 1) return listOf(
            null,
            currentSong,
            null,
        )

        return listOf(
            playbackManager.getPreviousMusic(),
            currentSong,
            playbackManager.getNextMusic()
        )
    }

    /**
     * Retrieve the album id of a music.
     */
    private fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) {
            getAlbumIdFromMusicIdUseCase(musicId)
        }
    }

    /**
     * Set the current music cover.
     */
    fun setCurrentMusicCover(cover: ImageBitmap?) {
        playbackManager.updateCover(cover = cover)
        colorThemeManager.setCurrentCover(cover = cover)
    }

    /**
     * Set the current music position.
     */
    fun seekTo(position: Int) {
        screenModelScope.launch {
            playbackManager.seekToPosition(position = position)
        }
    }

    /**
     * Set the playing state.
     */
    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    /**
     * Set the player mode.
     */
    fun changePlayerMode() {
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.changePlayerMode()
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
            screenModelScope.launch {
                val albumId: UUID? = getAlbumIdFromMusicId(musicId = currentMusic.musicId)
                albumId?.let {
                    _navigationState.value = PlayerNavigationState.ToAlbum(
                        albumId = it,
                    )
                }
            }
        }
    }

    fun navigateToRemoteLyricsSettings() {
        _navigationState.value = PlayerNavigationState.ToRemoteLyricsSettings
    }

    fun handleMultiSelectionBottomSheet() {
        screenModelScope.launch {
            val selectedIds = multiSelectionState.value.selectedIds
            if (selectedIds.size == 1) {
                val selectedMusic: Music = commonMusicUseCase.getFromId(musicId = selectedIds[0]).firstOrNull() ?: return@launch
                showMusicBottomSheet(selectedMusic = selectedMusic)
            } else {
                showMultiMusicBottomSheet()
            }
        }
    }
}