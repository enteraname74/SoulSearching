package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    fun observe(): Flow<SoulSearchingSettings>
    suspend fun upsert(settings: SoulSearchingSettings)
}
