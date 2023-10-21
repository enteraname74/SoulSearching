package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.EventUtils
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AllPlaylistsViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> playlistDao.getAllPlaylistsWithMusicsSortByNameAsc()
                        SortType.ADDED_DATE -> playlistDao.getAllPlaylistWithMusicsSortByAddedDateAsc()
                        SortType.NB_PLAYED -> playlistDao.getAllPlaylistWithMusicsSortByNbPlayedAsc()
                        else -> playlistDao.getAllPlaylistsWithMusicsSortByNameAsc()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> playlistDao.getAllPlaylistWithMusicsSortByNameDesc()
                        SortType.ADDED_DATE -> playlistDao.getAllPlaylistWithMusicsSortByAddedDateDesc()
                        SortType.NB_PLAYED -> playlistDao.getAllPlaylistWithMusicsSortByNbPlayedDesc()
                        else -> playlistDao.getAllPlaylistWithMusicsSortByNameDesc()
                    }
                }
                else -> playlistDao.getAllPlaylistsWithMusicsSortByNameAsc()
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

    fun onPlaylistEvent(event: PlaylistEvent) {
        EventUtils.onPlaylistEvent(
            event = event,
            _state = _state,
            state = state,
            playlistDao = playlistDao,
            musicPlaylistDao = musicPlaylistDao,
            _sortType = _sortType,
            _sortDirection = _sortDirection,
            imageCoverDao = imageCoverDao
        )
    }
}