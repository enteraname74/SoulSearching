package com.github.enteraname74.domain.repository

import kotlinx.coroutines.flow.Flow

interface CloudPreferencesRepository {
    fun observeUrl(): Flow<String?>

    suspend fun setUrl(url: String)
    suspend fun setLastSyncMillis(millis: Long)
    suspend fun setLastStatsSyncMillis(millis: Long)

    suspend fun getLastSyncMillis(): Long?
    suspend fun getLastStatsSyncMillis(): Long?

    suspend fun clearSyncsMillis()
}
