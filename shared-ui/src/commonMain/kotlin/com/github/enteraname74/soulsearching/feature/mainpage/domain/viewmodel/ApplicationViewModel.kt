package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ApplicationState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ApplicationViewModel(
    settings: SoulSearchingSettings,
) : ScreenModel {

    val state: StateFlow<ApplicationState> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION),
    ) { hasSongsBeenFetched, currentDbVersion ->
        when {
            currentDbVersion < LocalDatabaseVersion.version -> ApplicationState.AppMigration
            !hasSongsBeenFetched -> ApplicationState.FetchingSongs
            else -> ApplicationState.Data
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ApplicationState.AppMigration,
    )

    var isReadPermissionGranted by mutableStateOf(false)
    var isPostNotificationGranted by mutableStateOf(false)

}