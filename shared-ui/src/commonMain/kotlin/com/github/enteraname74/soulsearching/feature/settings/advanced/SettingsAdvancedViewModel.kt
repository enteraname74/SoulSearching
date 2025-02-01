package com.github.enteraname74.soulsearching.feature.settings.advanced

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.album.GetAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistsUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertAllArtistsUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertAllMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertAllPlaylistsUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsAdvancedViewModel(
    private val loadingManager: LoadingManager,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val upsertAllAlbumsUseCase: UpsertAllAlbumsUseCase,
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase,
    private val upsertAllMusicsUseCase: UpsertAllMusicsUseCase,
    private val upsertAllPlaylistsUseCase: UpsertAllPlaylistsUseCase,
    private val coverFileManager: CoverFileManager,
    private val playbackManager: PlaybackManager,
) : ScreenModel {
    private val _state: MutableStateFlow<SettingsAdvancedState> = MutableStateFlow(SettingsAdvancedState())
    val state: StateFlow<SettingsAdvancedState> = _state.asStateFlow()

    fun toggleImageReloadPanelExpandedState() {
        _state.value = _state.value.copy(
            isImageReloadPanelExpanded = !_state.value.isImageReloadPanelExpanded,
        )
    }

    fun reloadImages() {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                checkAndReloadSongs()
                checkAndReloadArtists()
                checkAndReloadAlbums()
                checkAndReloadPlaylists()
                _state.value = SettingsAdvancedState()
            }
        }
    }

    fun toggleReloadMusicsCovers() {
        _state.value = _state.value.copy(
            shouldReloadSongsCovers = !_state.value.shouldReloadSongsCovers,
        )
    }

    fun toggleReloadArtistsCovers() {
        _state.value = _state.value.copy(
            shouldReloadArtistsCovers = !_state.value.shouldReloadArtistsCovers,
        )
    }

    fun toggleReloadAlbumsCovers() {
        _state.value = _state.value.copy(
            shouldReloadAlbumsCovers = !_state.value.shouldReloadAlbumsCovers,
        )
    }

    fun toggleDeletePlaylistsCovers() {
        _state.value = _state.value.copy(
            shouldDeletePlaylistsCovers = !_state.value.shouldDeletePlaylistsCovers,
        )
    }

    private suspend fun checkAndReloadSongs() {
        if (_state.value.shouldReloadSongsCovers) {
            val allSongs: List<Music> = getAllMusicUseCase(DataMode.Local).first()

            upsertAllMusicsUseCase(
                allMusics = allSongs.map { music ->
                    playbackManager.updateMusic(music)
                    music.cover = coverFileManager.getCleanFileCoverForMusic(music)
                    music
                }
            )
        }
    }

    private suspend fun checkAndReloadPlaylists() {
        if (_state.value.shouldDeletePlaylistsCovers) {
            val allPlaylists: List<Playlist> = getAllPlaylistsUseCase(DataMode.Local).first()
            upsertAllPlaylistsUseCase(
                playlists = allPlaylists.map { playlist ->
                    playlist.cover = null
                    playlist
                }
            )
        }
    }

    private suspend fun checkAndReloadAlbums() {
        if (_state.value.shouldReloadAlbumsCovers) {
            val allAlbums: List<Album> = getAllAlbumsUseCase(DataMode.Local).first()
            upsertAllAlbumsUseCase(
                albums = allAlbums.map { album ->
                    album.cover = null
                    album
                }
            )
        }
    }

    private suspend fun checkAndReloadArtists() {
        if (_state.value.shouldReloadArtistsCovers) {
            val allArtists: List<Artist> = getAllArtistsUseCase(DataMode.Local).first()
            upsertAllArtistsUseCase(
                allArtists = allArtists.map { artist ->
                    artist.cover = null
                    artist
                }
            )
        }
    }
}