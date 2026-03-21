package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class RoomCloudPreferencesDataSourceImpl(
    private val appDatabase: AppDatabase,
) : CloudPreferencesDataSource {
    override fun observeUrl(): Flow<String?> =
        appDatabase.cloudPreferencesDao.observe().map { it?.url }

    override suspend fun setUrl(url: String) {
        val preferences: RoomCloudPreferences = appDatabase
            .cloudPreferencesDao
            .observe()
            .firstOrNull() ?: RoomCloudPreferences()

        appDatabase.cloudPreferencesDao.upsert(
            preferences = preferences.copy(
                url = url,
            )
        )
    }
}