package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedAlbumViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val playbackManager: PlaybackManager,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
) : ScreenModel {
    private var _selectedAlbumWithMusics: StateFlow<AlbumWithMusics?> = MutableStateFlow(
        AlbumWithMusics()
    )

    private val _state = MutableStateFlow(SelectedAlbumState())
    var state = combine(
        _state,
        getAllPlaylistWithMusicsUseCase()
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedAlbumState()
    )

    /**
     * Manage album events.
     */
    fun onEvent(event: SelectedAlbumEvent) {
        when (event) {
            is SelectedAlbumEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )

            is SelectedAlbumEvent.AddNbPlayed -> incrementNbPlayed(albumId = event.albumId)
            is SelectedAlbumEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is SelectedAlbumEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(isShown = event.isShown)
            is SelectedAlbumEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is SelectedAlbumEvent.SetMusicBottomSheetVisibility -> showOrHideMusicBottomSheet(isShown = event.isShown)
            is SelectedAlbumEvent.SetSelectedAlbum -> setSelectedAlbum(albumId = event.albumId)
            is SelectedAlbumEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
        }
    }

    /**
     * Set the selected album.
     */
    private fun setSelectedAlbum(albumId: UUID) {
        _selectedAlbumWithMusics = getAlbumWithMusicsUseCase(albumId = albumId)
            .stateIn(
                screenModelScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
            )

        state = combine(
            _state,
            _selectedAlbumWithMusics,
            getAllPlaylistWithMusicsUseCase()
        ) { state, album, playlists ->
            state.copy(
                albumWithMusics = album ?: AlbumWithMusics(),
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
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
     * Increment by one the number of time an album was played.
     */
    private fun incrementNbPlayed(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val album = _state.value.albumWithMusics.album
            upsertAlbumUseCase(
                album = album.copy(
                    nbPlayed = album.nbPlayed + 1,
                )
            )
        }
    }
}