package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.states.QuickAccessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * View model for managing all quick access.
 */
class AllQuickAccessViewModel(
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
) : ViewModel() {
    private var _musics = musicRepository.getAllMusicsFromQuickAccessAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _playlists = playlistRepository.getAllPlaylistsFromQuickAccessAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _albums = albumRepository.getAllAlbumWithArtistFromQuickAccessAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _artists = artistRepository.getAllArtistWithMusicsFromQuickAccessAsFlow()
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