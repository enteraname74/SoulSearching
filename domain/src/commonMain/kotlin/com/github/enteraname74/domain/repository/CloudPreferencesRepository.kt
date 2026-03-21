package com.github.enteraname74.domain.repository

import kotlinx.coroutines.flow.Flow

interface CloudPreferencesRepository {
    fun observeUrl(): Flow<String?>

    suspend fun setUrl(url: String)
}