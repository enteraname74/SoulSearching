package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

class DataModeDataSource(
    private val authLocalDataSource: AuthLocalDataSource,
    private val settings: SoulSearchingSettings,
) {
    fun switchDataMode(newDataMode: DataMode) {
        settings.set(
            key = SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE.key,
            value = newDataMode.value
        )
    }


    fun getCurrentDataModeWithUserCheck(): Flow<DataMode> =
        combine(
            authLocalDataSource.getUser(),
            settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE)
        ) { currentUser, dataMode ->
            DataMode.fromString(dataMode).takeIf { currentUser?.isValid() == true } ?: DataMode.Local
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCurrentDataMode(): Flow<DataMode> =
        settings.getFlowOn(SoulSearchingSettingsKeys.System.CURRENT_DATA_MODE).mapLatest { dataMode ->
            DataMode.fromString(dataMode) ?: DataMode.Local
        }
}