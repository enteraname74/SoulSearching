package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.soulsearching.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedPermissionState
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts

class SettingsAdvancedViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val commonReleaseUseCase: CommonReleaseUseCase,
    private val loadingManager: LoadingManager,
    private val settingsRepository: SettingsRepository,
    private val workDispatcher: WorkDispatcher,
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
        settingsRepository.observePlayer(),
        settingsRepository.observeRelease(),
    ) { player, release ->
        SettingsAdvancedPermissionState(
            isLyricsPermissionEnabled = player.isRemoteLyricsFetchEnabled,
            isGitHubReleaseFetchPermissionEnabled = release.isFetchReleaseFromGithubEnabled,
        )
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = SettingsAdvancedPermissionState(
            isLyricsPermissionEnabled = false,
            isGitHubReleaseFetchPermissionEnabled = false,
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

            SettingsAdvancedAction.ToggleCollectionsCovers -> {
                toggleDeleteCollectionsCovers()
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
        viewModelScope.launch {
            val enabled = settingsRepository.getSettings().playerSettings.isRemoteLyricsFetchEnabled
            settingsRepository.setRemoteLyricsFetchEnabled(!enabled)
        }
    }

    private fun toggleGitHubReleasePermission() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val newState = !settingsRepository.getSettings().release.isFetchReleaseFromGithubEnabled
            settingsRepository.setFetchReleaseFromGithubEnabled(newState)
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
                    text = CoreRes.string.activate_github_release_fetch_hint.toTextSpec(),
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
                    text = SoulTexts.activateRemoteLyricsFetchHint(),
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
        CoroutineScope(workDispatcher.dispatcher).launch {
            loadingManager.withLoading {
                checkAndReloadSongs()
                checkAndReloadArtists()
                checkAndReloadAlbums()
                checkAndReloadPlaylists()
                checkAndReloadCollections()
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

    private fun toggleDeleteCollectionsCovers() {
        _state.value = _state.value.copy(
            shouldDeleteCollectionsCovers = !_state.value.shouldDeleteCollectionsCovers,
        )
    }

    private suspend fun checkAndReloadSongs() {
        if (_state.value.shouldReloadSongsCovers) {
            commonMusicUseCase.cleanAllMusicCovers()
        }
    }

    private suspend fun checkAndReloadPlaylists() {
        if (_state.value.shouldDeletePlaylistsCovers) {
            commonPlaylistUseCase.cleanAllCovers()
        }
    }

    private suspend fun checkAndReloadCollections() {
        if (_state.value.shouldDeleteCollectionsCovers) {
            commonCollectionUseCase.cleanAllCovers()
        }
    }

    private suspend fun checkAndReloadAlbums() {
        if (_state.value.shouldReloadAlbumsCovers) {
            commonAlbumUseCase.cleanAllCovers()
        }
    }

    private suspend fun checkAndReloadArtists() {
        if (_state.value.shouldReloadArtistsCovers) {
            commonArtistUseCase.cleanAllCovers()
        }
    }
}
