package com.github.enteraname74.domain.repository

import kotlinx.coroutines.flow.Flow

interface CloudRepository {
    suspend fun clearLastUpdateDate()
    suspend fun setSearchMetadata(searchMetadata: Boolean)
    fun getSearchMetadata(): Flow<Boolean>
}