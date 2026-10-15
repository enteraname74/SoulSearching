package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoom
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.repository.datasource.SettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSettingsLocalDataSourceImpl(private val appDatabase: AppDatabase) : SettingsLocalDataSource {
    override fun observe(): Flow<SoulSearchingSettings> = appDatabase.settingsDao.observe().map { it.toDomain() }
    override suspend fun upsert(settings: SoulSearchingSettings) = appDatabase.settingsDao.upsert(settings.toRoom())
}
