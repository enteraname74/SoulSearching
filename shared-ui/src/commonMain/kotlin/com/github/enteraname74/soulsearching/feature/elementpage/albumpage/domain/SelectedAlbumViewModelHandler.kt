package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
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
 * Handler for managing the SelectedAlbumViewModel.
 */
class SelectedAlbumViewModelHandler(
    private val coroutineScope: CoroutineScope,
    private val albumRepository: AlbumRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val musicRepository: MusicRepository,
    private val playbackManager: PlaybackManager,
    playlistRepository: PlaylistRepository,
) : ViewModelHandler {
    private var _selectedAlbumWithMusics : StateFlow<AlbumWithMusics?> = MutableStateFlow(
        AlbumWithMusics()
    )

    private val _playlists = playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _state = MutableStateFlow(SelectedAlbumState())
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
        SelectedAlbumState()
    )

    /**
     * Manage album events.
     */
    fun onEvent(event: SelectedAlbumEvent) {
        when(event) {
            is SelectedAlbumEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )
            is SelectedAlbumEvent.AddNbPlayed -> incrementNbPlayed(albumId = event.albumId)
            is SelectedAlbumEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is SelectedAlbumEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(isShown = event.isShown)
            is SelectedAlbumEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is SelectedAlbumEvent.SetMusicBottomSheetVisibility ->showOrHideMusicBottomSheet(isShown = event.isShown)
            is SelectedAlbumEvent.SetSelectedAlbum -> setSelectedAlbum(albumId = event.albumId)
            is SelectedAlbumEvent.ToggleQuickAccessState -> toggleQuickAccessState(musicId = event.musicId)
        }
    }

    /**
     * Set the selected album.
     */
    private fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = albumRepository
            .getAlbumWithMusics(albumId = albumId)
            .stateIn(
                coroutineScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
            )

        state = combine(
            _state,
            _selectedAlbumWithMusics,
            _playlists
        ) { state, album, playlists ->
            state.copy(
                albumWithMusics = album ?: AlbumWithMusics(),
                allPlaylists = playlists
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedAlbumState()
        )

        _state.update {
            it.copy(
                albumWithMusics = _selectedAlbumWithMusics.value ?: AlbumWithMusics()
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
            val music = musicRepository.getMusicFromId(musicId = musicId)
            playbackManager.updateMusic(music = music)
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
     * Increment by one the number of time an album was played.
     */
    private fun incrementNbPlayed(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            albumRepository.updateNbPlayed(
                newNbPlayed = albumRepository.getNbPlayedOfAlbum(albumId) + 1,
                albumId = albumId
            )
        }
    }
}