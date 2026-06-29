package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.CloudPreferences
import kotlinx.coroutines.flow.Flow

interface CloudPreferencesDataSource {
    fun observeUrl(): Flow<String?>
    suspend fun setUrl(url: String)

    suspend fun setLastSyncMillis(millis: Long)

    fun observePreferences(): Flow<CloudPreferences?>
}