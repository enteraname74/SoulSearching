package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.soulsearching.domain.model.settings.CloudSoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    fun observe(): Flow<SoulSearchingSettings>
    fun observeCloud(): Flow<CloudSoulSearchingSettings>
    suspend fun getCloudUpdatedAfter(lastUpdateAt: Long?): CloudSoulSearchingSettings?
    suspend fun applyCloud(settings: CloudSoulSearchingSettings, lastSettingsSyncMillis: Long)
    suspend fun upsert(settings: SoulSearchingSettings)
}
