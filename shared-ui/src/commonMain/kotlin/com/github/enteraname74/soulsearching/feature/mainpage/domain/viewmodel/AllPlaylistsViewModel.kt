package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.*
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.PlaylistState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class AllPlaylistsViewModel(
    getAllPlaylistWithMusicsSortedUseCase: GetAllPlaylistWithMusicsSortedUseCase,
    private val playbackManager: PlaybackManager,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val deleteMusicFromPlaylistUseCase: DeleteMusicFromPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val getSelectablePlaylistWithMusicsForMusic: GetSelectablePlaylistWithMusicsForMusic,
    private val settings: SoulSearchingSettings
): ScreenModel {
    private val _sortType = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_PLAYLISTS_TYPE_KEY, SortType.NAME
        )
    )
    private val _sortDirection = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_PLAYLISTS_DIRECTION_KEY, SortDirection.ASC
        )
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            getAllPlaylistWithMusicsSortedUseCase(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(PlaylistState())

    // On combine nos 2 flows en un seul.
    val state = combine(
        _state,
        _playlists,
        _sortDirection,
        _sortType
    ) { state, playlists, sortDirection, sortType ->
        state.copy(
            playlists = playlists.map { it.toPlaylistWithMusicsNumber() },
            sortDirection = sortDirection,
            sortType = sortType
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    /**
     * Handle a playlist event.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.BottomSheet -> showOrHidBottomSheet(event)
            is PlaylistEvent.DeleteDialog -> showOrHideDeleteDialog(event)
            is PlaylistEvent.CreatePlaylistDialog -> showOrHideCreatePlaylistDialog(event)
            is PlaylistEvent.AddPlaylist -> addPlaylist(event)
            is PlaylistEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )
            is PlaylistEvent.RemoveMusicFromPlaylist -> removeMusicFromSelectedPlaylist(event)
            is PlaylistEvent.DeletePlaylist -> deleteSelectedPlaylist()
            is PlaylistEvent.SetSelectedPlaylist -> setSelectedPlaylist(event)
            is PlaylistEvent.TogglePlaylistSelectedState -> togglePlaylistSelectedState(event)
            is PlaylistEvent.PlaylistsSelection -> setSelectablePlaylistsForMusic(event)
            is PlaylistEvent.SetSortDirection -> setSortDirection(event)
            is PlaylistEvent.SetSortType -> setSortType(event)
            is PlaylistEvent.CreateFavoritePlaylist -> createFavoritePlaylist(event)
            is PlaylistEvent.UpdateQuickAccessState -> updateQuickAccessState()
            is PlaylistEvent.AddNbPlayed -> incrementNbPlayedOfPlaylist(event)
        }
    }

    /**
     * Show or hide the playlist bottom sheet.
     */
    private fun showOrHidBottomSheet(event: PlaylistEvent.BottomSheet) {
        _state.update {
            it.copy(
                isBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(event: PlaylistEvent.DeleteDialog) {
        _state.update {
            it.copy(
                isDeleteDialogShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the create playlist dialog.
     */
    private fun showOrHideCreatePlaylistDialog(event: PlaylistEvent.CreatePlaylistDialog) {
        _state.update {
            it.copy(
                isCreatePlaylistDialogShown = event.isShown
            )
        }
    }

    /**
     * Add a new playlist.
     */
    private fun addPlaylist(event: PlaylistEvent.AddPlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertPlaylistUseCase(
                Playlist(
                    playlistId = UUID.randomUUID(),
                    name = event.name
                )
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
            val music: Music = getMusicUseCase(musicId = musicId).first() ?: return@launch
            playbackManager.updateMusic(music = music)
            _state.update {
                it.copy(
                    multiplePlaylistSelected = ArrayList()
                )
            }
        }
    }

    /**
     * Remove a music from the selected playlist
     */
    private fun removeMusicFromSelectedPlaylist(event: PlaylistEvent.RemoveMusicFromPlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicFromPlaylistUseCase(
                musicId = event.musicId,
                playlistId = _state.value.selectedPlaylist.playlistId
            )
        }
    }

    /**
     * Delete the selected playlist
     */
    private fun deleteSelectedPlaylist() {
        CoroutineScope(Dispatchers.IO).launch {
            deletePlaylistUseCase(_state.value.selectedPlaylist)
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedPlaylist(event: PlaylistEvent.SetSelectedPlaylist) {
        _state.update {
            it.copy(
                selectedPlaylist = event.playlist
            )
        }
    }

    /**
     * Toggle a playlist selection state.
     */
    private fun togglePlaylistSelectedState(event: PlaylistEvent.TogglePlaylistSelectedState) {
        val newList = ArrayList(_state.value.multiplePlaylistSelected)
        if (event.playlistId in newList) newList.remove(event.playlistId)
        else newList.add(event.playlistId)

        _state.update {
            it.copy(
                multiplePlaylistSelected = newList
            )
        }
    }

    /**
     * Define the playlists where a music can be added.
     */
    private fun setSelectablePlaylistsForMusic(event: PlaylistEvent.PlaylistsSelection) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlists = getSelectablePlaylistWithMusicsForMusic(event.musicId)
            _state.update {
                it.copy(
                    multiplePlaylistSelected = ArrayList(),
                    playlistsWithMusics = playlists
                )
            }
        }
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(event: PlaylistEvent.SetSortDirection) {
        _sortDirection.value = event.type
        settings.setInt(
            key = SoulSearchingSettings.SORT_PLAYLISTS_DIRECTION_KEY,
            value = event.type
        )
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(event: PlaylistEvent.SetSortType) {
        _sortType.value = event.type
        settings.setInt(
            key = SoulSearchingSettings.SORT_PLAYLISTS_TYPE_KEY,
            value = event.type
        )
    }

    /**
     * Create the favorite playlist.
     */
    private fun createFavoritePlaylist(event: PlaylistEvent.CreateFavoritePlaylist) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertPlaylistUseCase(
                Playlist(
                    playlistId = UUID.randomUUID(),
                    name = event.name,
                    isFavorite = true
                )
            )
        }
    }

    /**
     * Update the quick access state of the selected playlist.
     */
    private fun updateQuickAccessState() {
        CoroutineScope(Dispatchers.IO).launch {
            upsertPlaylistUseCase(
                playlist = _state.value.selectedPlaylist.copy(
                    isInQuickAccess = !_state.value.selectedPlaylist.isInQuickAccess
                )
            )
        }
    }

    /**
     * Increment the number of time a playlist has been played.
     */
    private fun incrementNbPlayedOfPlaylist(event: PlaylistEvent.AddNbPlayed) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist: Playlist = getPlaylistUseCase(event.playlistId).first() ?: return@launch
            upsertPlaylistUseCase(
                playlist = playlist.copy(
                    nbPlayed = playlist.nbPlayed + 1
                )
            )
        }
    }
}