package com.github.enteraname74.soulsearching.feature.player.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.lyrics.GetLyricsOfSongUseCase
import com.github.enteraname74.domain.usecase.music.*
import com.github.enteraname74.domain.usecase.musicalbum.GetAlbumIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.musicartist.GetArtistIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Handler for managing the PlayerViewModel.
 */
class PlayerViewModel(
    private val playbackManager: PlaybackManager,
    settings: SoulSearchingSettings,
    private val colorThemeManager: ColorThemeManager,
    private val getLyricsOfSongUseCase: GetLyricsOfSongUseCase,
    private val isMusicInFavoritePlaylistUseCase: IsMusicInFavoritePlaylistUseCase,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val getArtistIdFromMusicIdUseCase: GetArtistIdFromMusicIdUseCase,
    private val getAlbumIdFromMusicIdUseCase: GetAlbumIdFromMusicIdUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    imageCoverRetriever: ImageCoverRetriever,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
    private val _state = MutableStateFlow(PlayerState())

    val state = combine(
        _state,
        getAllPlaylistWithMusicsUseCase(),
        imageCoverRetriever.allCovers,
        imageCoverRetriever.getImageBitmap(_state.value.currentMusic?.coverId),
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_PLAYER_SWIPE_ENABLED),
    ) { state, playlists, allCovers, currentMusicCover, isCoverSwipeEnabled ->
        state.copy(
            playlistsWithMusics = playlists,
            allCovers = allCovers,
            currentMusicCover = currentMusicCover,
            canSwipeCover = isCoverSwipeEnabled,
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlayerState()
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
        _navigationState.value = PlayerNavigationState.Idle
    }

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = PlayerNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = { state.value.playlistsWithMusics },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
        )

        playbackManager.setCallback(callback = object : PlaybackManager.Companion.Callback {
            override fun onPlayedListUpdated(playedList: List<Music>) {
                super.onPlayedListUpdated(playedList)
                onEvent(
                    PlayerEvent.SetPlayedList(
                        playedList = playedList
                    )
                )
            }

            override fun onPlayerModeChanged(playerMode: PlayerMode) {
                super.onPlayerModeChanged(playerMode)
                onEvent(
                    PlayerEvent.SetPlayerMode(
                        playerMode = playerMode
                    )
                )
            }

            override fun onCurrentPlayedMusicChanged(music: Music?) {
                super.onCurrentPlayedMusicChanged(music)
                onEvent(
                    PlayerEvent.SetCurrentMusic(
                        currentMusic = music
                    )
                )
            }

            override fun onCurrentMusicPositionChanged(position: Int) {
                super.onCurrentMusicPositionChanged(position)
                onEvent(
                    PlayerEvent.SetCurrentMusicPosition(
                        position = position
                    )
                )
            }

            override fun onPlayingStateChanged(isPlaying: Boolean) {
                super.onPlayingStateChanged(isPlaying)
                onEvent(
                    PlayerEvent.SetIsPlaying(
                        isPlaying = isPlaying
                    )
                )
            }

            override fun onCurrentMusicCoverChanged(cover: ImageBitmap?) {
                super.onCurrentMusicCoverChanged(cover)
                onEvent(
                    PlayerEvent.SetCurrentMusicCover(
                        cover = cover
                    )
                )
            }
        })
    }

    /**
     * Manage music events.
     */
    fun onEvent(event: PlayerEvent) {
        when (event) {
            PlayerEvent.ToggleFavoriteState -> toggleFavoriteState()
            PlayerEvent.GetLyrics -> setLyricsOfCurrentMusic()
            is PlayerEvent.SetCurrentMusic -> setCurrentMusic(music = event.currentMusic)
            is PlayerEvent.SetCurrentMusicCover -> setCurrentMusicCover(cover = event.cover)
            is PlayerEvent.SetCurrentMusicPosition -> setCurrentMusicPosition(position = event.position)
            is PlayerEvent.SetIsPlaying -> setIsPlaying(isPlaying = event.isPlaying)
            is PlayerEvent.SetPlayedList -> setPlayedList(playedList = event.playedList)
            is PlayerEvent.SetPlayerMode -> setPlayerMode(playerMode = event.playerMode)
        }
    }

    /**
     * Retrieve a list containing the current song and its around songs (previous and next).
     * If no songs are played, return a list containing null. If the played list contains only
     * the current song, it will return a list with only the current song.
     */
    private fun getAroundSongs(
        currentSong: Music?,
    ): List<Music?> {
        if (currentSong == null || _state.value.canSwipeCover == false) return emptyList()

        val currentSongIndex = playbackManager.currentMusicIndex

        if (currentSongIndex == -1) return listOf(null)

        if (playbackManager.playedList.size == 1) return listOf(
            currentSong
        )

        return listOf(
            playbackManager.getPreviousMusic(currentSongIndex),
            currentSong,
            playbackManager.getNextMusic(currentSongIndex)
        )
    }

    /**
     * Retrieve the artist id of a music.
     */
    fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) {
            getArtistIdFromMusicIdUseCase(musicId)
        }
    }

    /**
     * Retrieve the album id of a music.
     */
    fun getAlbumIdFromMusicId(musicId: UUID): UUID? {
        return runBlocking(context = Dispatchers.IO) {
            getAlbumIdFromMusicIdUseCase(musicId)
        }
    }

    /**
     * Set the lyrics of the current music.
     */
    private fun setLyricsOfCurrentMusic() {
        _state.value.currentMusic?.let { currentMusic ->
            CoroutineScope(Dispatchers.IO).launch {
                _state.update {
                    it.copy(
                        currentMusicLyrics = LyricsFetchState.FetchingLyrics
                    )
                }
                val lyrics = getLyricsOfSongUseCase(music = currentMusic)
                _state.update {
                    it.copy(
                        currentMusicLyrics = if (lyrics == null) LyricsFetchState.NoLyricsFound else LyricsFetchState.FoundLyrics(
                            lyrics = lyrics
                        )
                    )
                }
            }
        }
    }

    /**
     * Set the current music.
     */
    private fun setCurrentMusic(music: Music?) {
        _state.update {
            it.copy(
                currentMusic = music,
                aroundSongs = getAroundSongs(music)
            )
        }
        setFavoriteState()
    }

    /**
     * Set the current music cover.
     */
    private fun setCurrentMusicCover(cover: ImageBitmap?) {
        if (_state.value.currentMusicCover?.equals(cover) == true) return

        _state.update {
            it.copy(
                currentMusicCover = cover
            )
        }
        colorThemeManager.setCurrentCover(cover = cover)
    }

    /**
     * Set the current music position.
     */
    private fun setCurrentMusicPosition(position: Int) {
        _state.update {
            it.copy(
                currentMusicPosition = position
            )
        }
    }

    /**
     * Set the playing state.
     */
    private fun setIsPlaying(isPlaying: Boolean) {
        _state.update {
            it.copy(
                isPlaying = isPlaying
            )
        }
    }

    /**
     * Set the player mode.
     */
    private fun setPlayerMode(playerMode: PlayerMode) {
        _state.update {
            it.copy(
                playerMode = playerMode
            )
        }
    }

    /**
     * Set the played list.
     */
    private fun setPlayedList(playedList: List<Music>) {
        _state.update {
            it.copy(
                playedList = playedList
            )
        }
    }

    /**
     * Toggle the favorite status of the current music if there is one.
     */
    private fun toggleFavoriteState() {
        _state.value.currentMusic?.let { music ->
            CoroutineScope(Dispatchers.IO).launch {
                toggleMusicFavoriteStatusUseCase(musicId = music.musicId)
                setFavoriteState()
            }
        }
    }

    /**
     * Sets the value of the favorite state of the current music in the UI.
     */
    private fun setFavoriteState() {
        _state.value.currentMusic?.let { music ->
            CoroutineScope(Dispatchers.IO).launch {
                _state.update {
                    it.copy(
                        isCurrentMusicInFavorite = isMusicInFavoritePlaylistUseCase(musicId = music.musicId)
                    )
                }
            }
        }
    }
}