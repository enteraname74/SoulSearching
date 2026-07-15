package com.github.enteraname74.soulsearching.feature.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Platform
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.domain.util.PlatformUtils
import kotlinx.coroutines.launch

class MigrationViewModel(
    private val settings: SoulSearchingSettings,
    private val navScope: MigrationNavScope,
) : ViewModel() {
    init {
        viewModelScope.launch {
            settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION).collect { currentDbVersion ->
                if (currentDbVersion >= LocalDatabaseVersion.VERSION) {
                    if (settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY) && PlatformUtils.platform != Platform.Web) {
                        navScope.toMainApp()
                    } else {
                        navScope.toInitialFetch()
                    }
                }
            }
        }
    }
}
