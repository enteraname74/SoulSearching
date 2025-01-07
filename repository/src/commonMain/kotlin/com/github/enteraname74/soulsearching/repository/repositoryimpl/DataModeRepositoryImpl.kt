package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.DataModeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class DataModeRepositoryImpl(
    private val settings: SoulSearchingSettings
): DataModeRepository {
    override suspend fun switchDataMode(newDataMode: DataMode) {
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE.key,
            value = newDataMode.value
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentDataMode(): Flow<DataMode> =
        settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE).mapLatest { mode ->
            DataMode.fromString(mode) ?: DataMode.Local
        }
}