package com.github.soulsearching.elementpage.playlistpage.domain

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the SelectedPlaylistViewModel.
 */
class SelectedPlaylistViewModelHandler(
    private val coroutineScope: CoroutineScope,
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val playbackManager: PlaybackManager
) : ViewModelHandler {
    private var _selectedPlaylistMusics: StateFlow<PlaylistWithMusics?> = MutableStateFlow(
        PlaylistWithMusics()
    )

    private val _playlists = playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
        .stateIn(
            coroutineScope,
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
        coroutineScope,
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
            is SelectedPlaylistEvent.ToggleQuickAccessState -> toggleQuickAccessState(musicId = event.musicId)
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
            musicPlaylistRepository.deleteMusicFromPlaylist(
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
        _selectedPlaylistMusics = playlistRepository
            .getPlaylistWithMusicsAsFlow(playlistId = playlistId)
            .stateIn(
                coroutineScope, SharingStarted.WhileSubscribed(), PlaylistWithMusics()
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
            coroutineScope,
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
            playlistRepository.updateNbPlayed(
                newNbPlayed = playlistRepository.getNbPlayedOfPlaylist(playlistId) + 1,
                playlistId = playlistId
            )
        }
    }
}