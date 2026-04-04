package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.CloudPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface CloudPreferencesDataSource {
    fun observeUrl(): Flow<String?>
    suspend fun getUrl(): String = observeUrl().firstOrNull().orEmpty()
    suspend fun setUrl(url: String)

    suspend fun setLastSyncMillis(millis: Long)

    fun observePreferences(): Flow<CloudPreferences?>
}