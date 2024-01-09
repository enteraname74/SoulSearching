package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.events.handlers.PlaylistEventHandler
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * View model for managing all playlists.
 */
class AllPlaylistsViewModel(
    private val playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
                        SortType.ADDED_DATE -> playlistRepository.getAllPlaylistWithMusicsSortByAddedDateAscAsFlow()
                        SortType.NB_PLAYED -> playlistRepository.getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow()
                        else -> playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> playlistRepository.getAllPlaylistWithMusicsSortByNameDescAsFlow()
                        SortType.ADDED_DATE -> playlistRepository.getAllPlaylistWithMusicsSortByAddedDateDescAsFlow()
                        SortType.NB_PLAYED -> playlistRepository.getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow()
                        else -> playlistRepository.getAllPlaylistWithMusicsSortByNameDescAsFlow()
                    }
                }
                else -> playlistRepository.getAllPlaylistsWithMusicsSortByNameAscAsFlow()
            }
        }
    }.stateIn(
        viewModelScope,
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
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    private val playlistEventHandler = PlaylistEventHandler(
        privateState = _state,
        publicState = state,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        imageCoverRepository = imageCoverRepository,
        sortDirection = _sortDirection,
        sortType = _sortType,
        settings = settings
    )

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        playlistEventHandler.handleEvent(event)
    }
}