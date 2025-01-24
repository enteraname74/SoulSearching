package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.SoulResult
import kotlinx.coroutines.flow.Flow
import java.util.*

interface CloudRepository {
    suspend fun clearLastUpdateDate()
    suspend fun setSearchMetadata(searchMetadata: Boolean)
    fun getSearchMetadata(): Flow<Boolean>

    /**
     * Sync all data with the cloud.
     * Returns the list of music ids that were deleted if they no longer exist on the cloud.
     */
    suspend fun syncDataWithCloud(): SoulResult<List<UUID>>
}