package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.QuickAccessState
import com.github.enteraname74.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Handler for managing the AllQuickAccessViewModel.
 */
class AllQuickAccessViewModelHandler(
    coroutineScope: CoroutineScope,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
) : ViewModelHandler {
    private var _musics = musicRepository.getAllMusicsFromQuickAccessAsFlow()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _playlists = playlistRepository.getAllPlaylistsFromQuickAccessAsFlow()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _albums = albumRepository.getAllAlbumWithArtistFromQuickAccess()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())
    private var _artists = artistRepository.getAllArtistWithMusicsFromQuickAccessAsFlow()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())

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
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        QuickAccessState()
    )
}