package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.CloudPreferences
import kotlinx.coroutines.flow.Flow

interface CloudPreferencesRepository {
    fun observeUrl(): Flow<String?>

    suspend fun setUrl(url: String)
    suspend fun setLastSyncMillis(millis: Long)

    fun observePreferences(): Flow<CloudPreferences?>

    suspend fun clearLastSyncMillis()
}