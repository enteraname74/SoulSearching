package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.soulsearching.domain.model.settings.CloudSoulSearchingSettings

interface SettingsRemoteDataSource {
    suspend fun get(lastUpdateAt: Long?): CloudSoulSearchingSettings?
    suspend fun upsert(settings: CloudSoulSearchingSettings): CloudSoulSearchingSettings
}
