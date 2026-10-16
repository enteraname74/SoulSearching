package com.github.enteraname74.localdb.datasourceimpl

import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import com.github.enteraname74.localdb.model.toRoom
import com.github.enteraname74.soulsearching.domain.model.settings.CloudSoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.toCloud
import com.github.enteraname74.soulsearching.domain.model.settings.withCloud
import com.github.enteraname74.soulsearching.repository.datasource.SettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

class RoomSettingsLocalDataSourceImpl(private val appDatabase: AppDatabase) : SettingsLocalDataSource {
    override fun observe(): Flow<SoulSearchingSettings> = appDatabase.settingsDao.observe().map { it.toDomain() }
    override fun observeCloud(): Flow<CloudSoulSearchingSettings> =
        observe().map(SoulSearchingSettings::toCloud).distinctUntilChanged()

    override suspend fun getCloudUpdatedAfter(lastUpdateAt: Long?): CloudSoulSearchingSettings? =
        appDatabase.settingsDao.get().toDomain().toCloud().takeIf {
            lastUpdateAt == null || it.updatedAtMillis > lastUpdateAt
        }

    override suspend fun applyCloud(settings: CloudSoulSearchingSettings, lastSettingsSyncMillis: Long) {
        appDatabase.useWriterConnection { connection ->
            connection.immediateTransaction {
                val current = appDatabase.settingsDao.get().toDomain()
                appDatabase.settingsDao.upsert(current.withCloud(settings).toRoom())
                val preferences = appDatabase.cloudPreferencesDao.get() ?: RoomCloudPreferences()
                appDatabase.cloudPreferencesDao.upsert(
                    preferences.copy(lastSettingsSyncMillis = lastSettingsSyncMillis)
                )
            }
        }
    }

    override suspend fun upsert(settings: SoulSearchingSettings) = appDatabase.settingsDao.upsert(settings.toRoom())
}
