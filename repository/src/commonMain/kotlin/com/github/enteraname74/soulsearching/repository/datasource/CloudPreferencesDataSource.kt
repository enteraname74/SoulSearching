package com.github.enteraname74.soulsearching.repository.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface CloudPreferencesDataSource {
    fun observeUrl(): Flow<String?>
    suspend fun getUrl(): String = observeUrl().firstOrNull().orEmpty()
    suspend fun setUrl(url: String)

    suspend fun setLastSyncMillis(millis: Long)
    suspend fun setLastStatsSyncMillis(millis: Long)

    suspend fun getLastSyncMillis(): Long?

    suspend fun getLastStatsSyncMillis(): Long?

    suspend fun clearSyncsMillis()
}
