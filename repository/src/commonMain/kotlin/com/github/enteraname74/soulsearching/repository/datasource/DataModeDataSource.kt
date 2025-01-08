package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class DataModeDataSource(
    private val settings: SoulSearchingSettings,
) {
    fun switchDataMode(newDataMode: DataMode) {
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE.key,
            value = newDataMode.value
        )
    }
    fun getCurrentDataMode(): Flow<DataMode> =
        settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE).mapLatest { mode ->
            DataMode.fromString(mode) ?: DataMode.Local
        }
}