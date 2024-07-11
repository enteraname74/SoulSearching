package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedPlaylistViewModel(
    private val playbackManager: PlaybackManager,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val deleteMusicFromPlaylistUseCase: DeleteMusicFromPlaylistUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
) : ScreenModel {
    private var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics?> = MutableStateFlow(
        PlaylistWithMusics()
    )

    private val _playlists = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _state = MutableStateFlow(SelectedPlaylistState())
    var state = combine(
        _state,
        _playlists
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedPlaylistState()
    )

    /**
     * Handles events of the selected playlist screen.
     */
    fun onEvent(event: SelectedPlaylistEvent) {
        when(event) {
            is SelectedPlaylistEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )
            is SelectedPlaylistEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is SelectedPlaylistEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(isShown = event.isShown)
            is SelectedPlaylistEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is SelectedPlaylistEvent.SetMusicBottomSheetVisibility -> showOrHideMusicBottomSheet(isShown = event.isShown)
            is SelectedPlaylistEvent.SetSelectedPlaylist -> setSelectedPlaylist(playlistId = event.playlistId)
            is SelectedPlaylistEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
            is SelectedPlaylistEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
            is SelectedPlaylistEvent.RemoveMusicFromPlaylist -> removeMusicFromPlaylist(
                musicId = event.musicId,
                playlistId = event.playlistId
            )
            is SelectedPlaylistEvent.SetRemoveFromPlaylistDialogVisibility -> showOrHideRemoveMusicFromPlaylistDialog(
                isShown = event.isShown
            )
        }
    }

    /**
     * Remove a music from a playlist.
     */
    private fun removeMusicFromPlaylist(musicId: UUID, playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicFromPlaylistUseCase(
                musicId = musicId,
                playlistId = playlistId
            )
        }
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                upsertMusicIntoPlaylistUseCase(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            getMusicUseCase(musicId = musicId).first()?.let { music ->
                playbackManager.updateMusic(music = music)
            }
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicUseCase(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertMusicUseCase(
                music = music.copy(
                    isInQuickAccess = !music.isInQuickAccess,
                )
            )
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

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideRemoveMusicFromPlaylistDialog(isShown: Boolean) {
        _state.update {
            it.copy(
                isRemoveFromPlaylistDialogShown = isShown
            )
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedPlaylist(playlistId: UUID) {
        _selectedPlaylistMusics = getPlaylistWithMusicsUseCase(playlistId = playlistId)
            .stateIn(
                screenModelScope, SharingStarted.WhileSubscribed(), PlaylistWithMusics()
            )

        state = combine(
            _state,
            _selectedPlaylistMusics,
            _playlists
        ) { state, playlist, playlists ->
            state.copy(
                playlistWithMusics = playlist,
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaylistState()
        )

        _state.update {
            it.copy(
                playlistWithMusics = _selectedPlaylistMusics.value
            )
        }
    }

    /**
     * Increment by one the number of time a playlist was played.
     */
    private fun incrementNbPlayed(playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist: Playlist = getPlaylistUseCase(playlistId = playlistId).first() ?: return@launch
            upsertPlaylistUseCase(
                playlist = playlist.copy(
                    nbPlayed = playlist.nbPlayed + 1,
                )
            )
        }
    }
}