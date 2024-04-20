package com.github.soulsearching.mainpage.domain.viewmodelhandler

import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.events.handlers.PlaylistEventHandler
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.mainpage.domain.model.SortDirection
import com.github.soulsearching.mainpage.domain.model.SortType
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * Handler for managing the AllPlaylistsViewModel.
 */
class AllPlaylistsViewModelHandler(
    coroutineScope: CoroutineScope,
    private val playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    musicRepository: MusicRepository,
    playbackManager: PlaybackManager,
    settings: SoulSearchingSettings
): ViewModelHandler {
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
        coroutineScope,
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
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistState()
    )

    private val playlistEventHandler = PlaylistEventHandler(
        privateState = _state,
        publicState = state,
        sortType = _sortType,
        sortDirection = _sortDirection,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        settings = settings,
        musicRepository = musicRepository,
        playbackManager = playbackManager
    )

    /**
     * Manage playlist events.
     */
    fun onPlaylistEvent(event: PlaylistEvent) {
        playlistEventHandler.handleEvent(event)
    }
}