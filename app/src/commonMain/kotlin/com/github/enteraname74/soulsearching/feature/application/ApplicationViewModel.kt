package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.model.Platform
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.domain.util.PlatformUtils
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import kotlinx.coroutines.launch

class ApplicationViewModel(
    private val settings: SoulSearchingSettings,
    private val musicLinkHandler: MusicLinkHandler,
) : ViewModel() {
    init {
        viewModelScope.launch {
            if (PlatformUtils.platform == Platform.Web) {
                settings.set(
                    SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
                    true,
                )
            }
        }
    }
    val initialRoute: NavKey =
        when {
            settings.get(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION) < LocalDatabaseVersion.VERSION ->
                MigrationDestination
            !settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY) && PlatformUtils.platform != Platform.Web ->
                AppInitSongFetchingDestination
            else ->
                MainAppDestination
        }

    var isReadPermissionGranted: Boolean by mutableStateOf(false)
    var isPostNotificationGranted: Boolean by mutableStateOf(false)

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
