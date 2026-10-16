package com.github.enteraname74.soulsearching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.domain.util.isWeb
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import com.github.enteraname74.soulsearching.feature.onboarding.OnboardingDestination
import com.github.enteraname74.soulsearching.feature.permission.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApplicationViewModel(
    private val settingsRepository: SettingsRepository,
    private val musicLinkHandler: MusicLinkHandler,
    permissionManager: PermissionManager,
) : ViewModel() {
    private val _state = MutableStateFlow(
        ApplicationState(
            hasNotificationPermissions = permissionManager.isPostNotificationGranted(),
            hasReadStoragePermission = permissionManager.isReadStorageGranted(),
            initialRoute = null,
        )
    )
    val state: StateFlow<ApplicationState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val initialRoute = when {
                /*
                Onboarding is only useful for platforms with local songs (android and desktop)
                 */
                !settingsRepository.getSettings().hasMusicsBeenFetched && !SoulPlatformUtils.isWeb ->
                    OnboardingDestination
                else -> {
                    if (SoulPlatformUtils.isWeb) {
                        // For web platforms, we consider the fetching of musics to be already done (there is no need for one)
                        settingsRepository.setHasMusicsBeenFetched(true)
                    }
                    MainAppDestination
                }
            }
            _state.update { it.copy(initialRoute = initialRoute) }
        }
    }

    fun onReadStorageStateChanged(granted: Boolean) {
        _state.update { it.copy(hasReadStoragePermission = granted) }
    }

    fun onNotificationStateChanged(granted: Boolean) {
        _state.update { it.copy(hasNotificationPermissions = granted) }
    }

    private suspend fun isApplicationReady(): Boolean = settingsRepository.getSettings().hasMusicsBeenFetched

    fun handleMusicLink(link: String) {
        viewModelScope.launch {
            if (!isApplicationReady()) return@launch
            musicLinkHandler.handleLink(link)
        }
    }
}
