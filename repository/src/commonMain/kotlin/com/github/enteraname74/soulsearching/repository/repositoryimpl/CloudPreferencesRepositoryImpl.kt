package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.CloudPreferences
import com.github.enteraname74.soulsearching.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class CloudPreferencesRepositoryImpl(
    private val dataSource: CloudPreferencesDataSource
) : CloudPreferencesRepository {
    override fun observePreferences(): Flow<CloudPreferences> =
        dataSource.observePreferences()

    override fun observeUrl(): Flow<String?> =
        dataSource.observeUrl()

    override suspend fun setUrl(url: String) {
        dataSource.setUrl(url)
    }

    override suspend fun setLastStatsSyncMillis(millis: Long) {
        dataSource.setLastStatsSyncMillis(millis)
    }

    override suspend fun getLastStatsSyncMillis(): Long? =
        dataSource.getLastStatsSyncMillis()

    override suspend fun setLastSyncMillis(millis: Long) {
        dataSource.setLastSyncMillis(millis)
    }

    override suspend fun getLastSyncMillis(): Long? =
        dataSource.getLastSyncMillis()

    override suspend fun clearSyncsMillis() {
        dataSource.clearSyncsMillis()
    }
}
