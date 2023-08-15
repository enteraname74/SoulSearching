package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.states.QuickAccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllQuickAccessViewModel @Inject constructor(
    musicDao: MusicDao,
    playlistDao: PlaylistDao,
    albumDao: AlbumDao,
    artistDao: ArtistDao,
) : ViewModel() {
    private var _musics = musicDao.getAllMusicsFromQuickAccess()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _playlists = playlistDao.getAllPlaylistsFromQuickAccess()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _albums = albumDao.getAllAlbumsFromQuickAccess()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _artists = artistDao.getAllArtistsFromQuickAccess()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())

    private val _state = MutableStateFlow(QuickAccessState())

    val state = combine(
        _state,
        _musics,
        _playlists,
        _albums,
        _artists
    ) { state, musics, playlists, albums, artists ->
        state.copy(
            allQuickAccess = musics + playlists.map { it.toPlaylistWithMusicsNumber() } + albums + artists
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        QuickAccessState()
    )
}