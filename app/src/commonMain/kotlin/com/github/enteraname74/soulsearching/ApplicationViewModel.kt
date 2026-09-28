package com.github.enteraname74.soulsearching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.domain.util.LocalDatabaseVersion
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.domain.util.isWeb
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import com.github.enteraname74.soulsearching.feature.onboarding.OnboardingDestination
import com.github.enteraname74.soulsearching.feature.permission.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApplicationViewModel(
    private val settings: SoulSearchingSettings,
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
                settings.get(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION) < LocalDatabaseVersion.VERSION ->
                    MigrationDestination
                /*
                Onboarding is only useful for platforms with local songs (android and desktop)
                 */
                !settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY) && !SoulPlatformUtils.isWeb ->
                    OnboardingDestination
                else -> {
                    if (SoulPlatformUtils.isWeb) {
                        // For web platforms, we consider the fetching of musics to be already done (there is no need for one)
                        settings.set(
                            key = SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
                            value = true,
                        )
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

    private fun isApplicationReady(): Boolean =
        settings.get(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION) == LocalDatabaseVersion.VERSION
            && settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY)

    fun handleMusicLink(link: String) {
        viewModelScope.launch {
            if (!isApplicationReady()) return@launch
            musicLinkHandler.handleLink(link)
        }
    }
}