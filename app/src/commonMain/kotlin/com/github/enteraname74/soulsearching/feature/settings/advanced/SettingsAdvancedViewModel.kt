package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedPermissionState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedState
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class SettingsAdvancedViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonReleaseUseCase: CommonReleaseUseCase,
    private val loadingManager: LoadingManager,
    private val coverFileManager: CoverFileManager,
    private val playbackManager: PlaybackManager,
    private val settings: SoulSearchingSettings,
    destination: SettingsAdvancedDestination
) : ViewModel() {
    private val focusedElement = destination.focusedElement
    private val _state: MutableStateFlow<SettingsAdvancedState> = MutableStateFlow(
        SettingsAdvancedState(
            focusedElement = focusedElement,
        )
    )
    val state: StateFlow<SettingsAdvancedState> = _state.asStateFlow()

    val permissionState: StateFlow<SettingsAdvancedPermissionState> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED),
        settings.getFlowOn(SoulSearchingSettingsKeys.Release.IS_FETCH_RELEASE_FROM_GITHUB_ENABLED),
    ) { lyricsPermission, githubPermission ->
        SettingsAdvancedPermissionState(
            isLyricsPermissionEnabled = lyricsPermission,
            isGitHubReleaseFetchPermissionEnabled = githubPermission,
        )
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = SettingsAdvancedPermissionState(
            isLyricsPermissionEnabled = settings.get(SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED),
            isGitHubReleaseFetchPermissionEnabled = settings.get(SoulSearchingSettingsKeys.Release.IS_FETCH_RELEASE_FROM_GITHUB_ENABLED),
        ),
    )

    private val _navigationState: MutableStateFlow<SettingsAdvancedNavigationState> = MutableStateFlow(
        SettingsAdvancedNavigationState.Idle
    )
    val navigationState: StateFlow<SettingsAdvancedNavigationState> = _navigationState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    fun onAction(action: SettingsAdvancedAction) {
        when (action) {
            SettingsAdvancedAction.ReloadImages -> {
                reloadImages()
            }

            SettingsAdvancedAction.ShowLyricsPermissionDialog -> {
                showLyricsPermissionDialog()
            }

            SettingsAdvancedAction.ToMultipleArtists -> {
                navigateToMultipleArtists()
            }

            SettingsAdvancedAction.ToArtistCoverMethod -> {
                navigateToArtistCoverMethod()
            }

            SettingsAdvancedAction.ToggleAlbumsCovers -> {
                toggleReloadAlbumsCovers()
            }

            SettingsAdvancedAction.ToggleArtistsCovers -> {
                toggleReloadArtistsCovers()
            }

            SettingsAdvancedAction.ToggleExpandReloadImage -> {
                toggleImageReloadPanelExpandedState()
            }

            SettingsAdvancedAction.ToggleLyricsPermission -> {
                toggleLyricsPermission()
            }

            SettingsAdvancedAction.ToggleMusicsCover -> {
                toggleReloadMusicsCovers()
            }

            SettingsAdvancedAction.TogglePlaylistsCovers -> {
                toggleDeletePlaylistsCovers()
            }

            SettingsAdvancedAction.ToggleGithubReleaseFetchPermission -> {
                toggleGitHubReleasePermission()
            }

            SettingsAdvancedAction.ShowGitHubReleasePermissionDialog -> {
                showGitHubReleasePermissionDialog()
            }

            SettingsAdvancedAction.NavigateBack -> {
                _navigationState.value = SettingsAdvancedNavigationState.NavigateBack
            }
        }
    }

    fun consumeNavigation() {
        _navigationState.value = SettingsAdvancedNavigationState.Idle
    }

    private fun toggleLyricsPermission() {
        settings.set(
            key = SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED.key,
            value = !settings.get(SoulSearchingSettingsKeys.Player.IS_REMOTE_LYRICS_FETCH_ENABLED)
        )
    }

    private fun toggleGitHubReleasePermission() {
        CoroutineScope(Dispatchers.IO).launch {
            val newState = !settings.get(SoulSearchingSettingsKeys.Release.IS_FETCH_RELEASE_FROM_GITHUB_ENABLED)
            settings.set(
                key = SoulSearchingSettingsKeys.Release.IS_FETCH_RELEASE_FROM_GITHUB_ENABLED.key,
                value = newState
            )
            if (!newState) {
                commonReleaseUseCase.deleteLatest()
            } else {
                commonReleaseUseCase.fetchLatest()
            }
        }
    }

    private fun showGitHubReleasePermissionDialog() {
        _dialogState.value = object : SoulDialog {
            @Composable
            override fun Dialog() {
                SoulAlertDialog(
                    text = strings.activateGithubReleaseFetchHint,
                    confirmAction = {
                        _dialogState.value = null
                    },
                    dismissAction = {
                        _dialogState.value = null
                    }
                )
            }
        }
    }

    private fun navigateToMultipleArtists() {
        _navigationState.value = SettingsAdvancedNavigationState.ToMultipleArtists
    }

    private fun navigateToArtistCoverMethod() {
        _navigationState.value = SettingsAdvancedNavigationState.ToArtistCoverMethod
    }

    private fun showLyricsPermissionDialog() {
        _dialogState.value = object : SoulDialog {
            @Composable
            override fun Dialog() {
                SoulAlertDialog(
                    text = strings.activateRemoteLyricsFetchHint,
                    confirmAction = {
                        _dialogState.value = null
                    },
                    dismissAction = {
                        _dialogState.value = null
                    }
                )
            }
        }
    }

    private fun toggleImageReloadPanelExpandedState() {
        _state.value = _state.value.copy(
            isImageReloadPanelExpanded = !_state.value.isImageReloadPanelExpanded,
        )
    }

    private fun reloadImages() {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                checkAndReloadSongs()
                checkAndReloadArtists()
                checkAndReloadAlbums()
                checkAndReloadPlaylists()
                _state.value = SettingsAdvancedState(
                    focusedElement = focusedElement,
                )
            }
        }
    }

    private fun toggleReloadMusicsCovers() {
        _state.value = _state.value.copy(
            shouldReloadSongsCovers = !_state.value.shouldReloadSongsCovers,
        )
    }

    private fun toggleReloadArtistsCovers() {
        _state.value = _state.value.copy(
            shouldReloadArtistsCovers = !_state.value.shouldReloadArtistsCovers,
        )
    }

    private fun toggleReloadAlbumsCovers() {
        _state.value = _state.value.copy(
            shouldReloadAlbumsCovers = !_state.value.shouldReloadAlbumsCovers,
        )
    }

    private fun toggleDeletePlaylistsCovers() {
        _state.value = _state.value.copy(
            shouldDeletePlaylistsCovers = !_state.value.shouldDeletePlaylistsCovers,
        )
    }

    private suspend fun checkAndReloadSongs() {
        if (_state.value.shouldReloadSongsCovers) {
            val allSongs: List<Music> = commonMusicUseCase.getAll().first()

            commonMusicUseCase.upsertAll(
                allMusics = allSongs.map { music ->
                    playbackManager.updateMusic(music)
                    music.copy(
                        cover = coverFileManager.getCleanFileCoverForMusic(music)
                    )
                }
            )
        }
    }

    private suspend fun checkAndReloadPlaylists() {
        if (_state.value.shouldDeletePlaylistsCovers) {
            val allPlaylists: List<Playlist> = commonPlaylistUseCase.getAll().first()
            commonPlaylistUseCase.upsertAll(
                playlists = allPlaylists.map { playlist ->
                    playlist.copy(
                        cover = null
                    )
                }
            )
        }
    }

    private suspend fun checkAndReloadAlbums() {
        if (_state.value.shouldReloadAlbumsCovers) {
            val allAlbums: List<Album> = commonAlbumUseCase.getAll().first()
            commonAlbumUseCase.upsertAll(
                albums = allAlbums.map { album ->
                    album.copy(
                        cover = null
                    )
                }
            )
        }
    }

    private suspend fun checkAndReloadArtists() {
        if (_state.value.shouldReloadArtistsCovers) {
            val allArtists: List<Artist> = commonArtistUseCase.getAll().first()
            commonArtistUseCase.upsertAll(
                allArtists = allArtists.map { artist ->
                    artist.cover?.ifCoverFile { coverFile ->
                        artist.copy(
                            cover = coverFile.copy(fileCoverId = null)
                        )
                    } ?: artist
                }
            )
        }
    }
}