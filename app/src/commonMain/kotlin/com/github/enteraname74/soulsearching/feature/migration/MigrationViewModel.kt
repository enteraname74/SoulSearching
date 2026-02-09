package com.github.enteraname74.soulsearching.feature.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import kotlinx.coroutines.launch

class MigrationViewModel(
    private val settings: SoulSearchingSettings,
    private val navScope: MigrationNavScope,
): ViewModel() {
    init {
        viewModelScope.launch {
            settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DB_VERSION).collect { currentDbVersion ->
                if (currentDbVersion < LocalDatabaseVersion.VERSION) {
                    navScope.toMainApp()
                }
            }
        }
    }
}