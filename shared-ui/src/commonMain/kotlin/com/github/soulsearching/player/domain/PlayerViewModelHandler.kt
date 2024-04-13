package com.github.soulsearching.player.domain

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.LyricsProvider
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.utils.ColorPaletteUtils
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.player.domain.model.LyricsFetchState
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.player.domain.model.PlayerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the PlayerViewModel.
 */
class PlayerViewModelHandler(
    private val musicRepository: MusicRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
    private val colorThemeManager: ColorThemeManager,
    private val lyricsProvider: LyricsProvider,
    coroutineScope: CoroutineScope
) : ViewModelHandler {
    private val _state = MutableStateFlow(PlayerState())

    private val _playlists = playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val state = combine(
        _state,
        _playlists
    ) { state, playlists ->
        state.copy(
            playlistsWithMusics = playlists
        )
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        PlayerState()
    )

    @OptIn(ExperimentalMaterialApi::class)
    val playerDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    @OptIn(ExperimentalMaterialApi::class)
    val musicListDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    init {
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
            is PlayerEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is PlayerEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(
                isShown = event.isShown
            )

            is PlayerEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is PlayerEvent.SetMusicBottomSheetVisibility -> showOrHideMusicBottomSheet(isShown = event.isShown)
            is PlayerEvent.ToggleQuickAccessState -> toggleQuickAccessState(musicId = event.musicId)
            is PlayerEvent.SetCurrentMusic -> setCurrentMusic(music = event.currentMusic)
            is PlayerEvent.SetCurrentMusicCover -> setCurrentMusicCover(cover = event.cover)
            is PlayerEvent.SetCurrentMusicPosition -> setCurrentMusicPosition(position = event.position)
            is PlayerEvent.SetIsPlaying -> setIsPlaying(isPlaying = event.isPlaying)
            is PlayerEvent.SetPlayedList -> setPlayedList(playedList = event.playedList)
            is PlayerEvent.SetPlayerMode -> setPlayerMode(playerMode = event.playerMode)
            is PlayerEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )
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
                val lyrics = lyricsProvider.getLyricsOfSong(music = currentMusic)
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
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                musicPlaylistRepository.insertMusicIntoPlaylist(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
        }
    }

    /**
     * Set the current music.
     */
    private fun setCurrentMusic(music: Music?) {
        _state.update {
            it.copy(
                currentMusic = music
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
        colorThemeManager.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
            image = cover
        )
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
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.delete(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.toggleQuickAccessState(musicId = musicId)
        }
    }

    /**
     * Toggle the favorite status of the current music if there is one.
     */
    private fun toggleFavoriteState() {
        _state.value.currentMusic?.let { music ->
            CoroutineScope(Dispatchers.IO).launch {
                musicRepository.toggleFavoriteState(musicId = music.musicId)
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
                        isCurrentMusicInFavorite = musicRepository.getMusicFromFavoritePlaylist(
                            musicId = music.musicId
                        ) != null
                    )
                }
            }
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(isShown: Boolean) {
        _state.update {
            it.copy(
                isDeleteMusicDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = isShown
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isMusicBottomSheetShown = isShown
            )
        }
    }
}