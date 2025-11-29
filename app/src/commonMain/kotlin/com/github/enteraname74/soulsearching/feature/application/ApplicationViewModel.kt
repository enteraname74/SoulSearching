package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination

class ApplicationViewModel(
    settings: SoulSearchingSettings,
) : ViewModel() {
    val initialRoute: NavKey =
        when {
            settings.get(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION) < LocalDatabaseVersion.VERSION ->
                MigrationDestination
            !settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY) ->
                AppInitSongFetchingDestination
            else ->
                MainAppDestination
        }

    var isReadPermissionGranted by mutableStateOf(false)
    var isPostNotificationGranted by mutableStateOf(false)
}