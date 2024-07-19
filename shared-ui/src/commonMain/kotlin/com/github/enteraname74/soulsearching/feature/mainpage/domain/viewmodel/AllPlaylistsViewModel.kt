package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.*
import com.github.enteraname74.soulsearching.commondelegate.PlaylistBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.PlaylistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllPlaylistsNavigationState
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
    private val getSelectablePlaylistWithMusicsForMusicUseCase: GetSelectablePlaylistWithMusicsForMusicUseCase,
    private val settings: SoulSearchingSettings,
    private val playlistBottomSheetDelegateImpl: PlaylistBottomSheetDelegateImpl,
): ScreenModel, PlaylistBottomSheetDelegate by playlistBottomSheetDelegateImpl {
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

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<AllPlaylistsNavigationState> = MutableStateFlow(
        AllPlaylistsNavigationState.Idle
    )
    val navigationState: StateFlow<AllPlaylistsNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = AllPlaylistsNavigationState.Idle
    }

    init {
        playlistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyPlaylist = { _navigationState.value = AllPlaylistsNavigationState.ToModifyPlaylist(it) }
        )
    }

    /**
     * Handle a playlist event.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.CreatePlaylistDialog -> showOrHideCreatePlaylistDialog(event)
            is PlaylistEvent.AddPlaylist -> addPlaylist(event)
            is PlaylistEvent.SetSelectedPlaylist -> setSelectedPlaylist(event)
            is PlaylistEvent.SetSortDirection -> setSortDirection(event)
            is PlaylistEvent.SetSortType -> setSortType(event)
            is PlaylistEvent.CreateFavoritePlaylist -> createFavoritePlaylist(event)
            is PlaylistEvent.AddNbPlayed -> incrementNbPlayedOfPlaylist(event)
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