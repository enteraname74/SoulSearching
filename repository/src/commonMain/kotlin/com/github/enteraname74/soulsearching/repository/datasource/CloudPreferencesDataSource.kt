package com.github.enteraname74.soulsearching.repository.datasource

import kotlinx.coroutines.flow.Flow

interface CloudPreferencesDataSource {
    fun observeUrl(): Flow<String?>
    suspend fun setUrl(url: String)
}