package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class ApplicationViewModel(
    settings: SoulSearchingSettings,
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
) : ScreenModel {
    val shouldShowNewVersionPin: StateFlow<Boolean> = shouldInformOfNewReleaseUseCase()
        .stateIn(
            scope = screenModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val state: StateFlow<ApplicationState> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION),
    ) { hasSongsBeenFetched, currentDbVersion ->
        when {
            currentDbVersion < LocalDatabaseVersion.VERSION -> ApplicationState.AppMigration
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