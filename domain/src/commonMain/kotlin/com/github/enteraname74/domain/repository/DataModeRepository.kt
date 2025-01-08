package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.DataMode
import kotlinx.coroutines.flow.Flow

interface DataModeRepository {
    suspend fun switchDataMode(newDataMode: DataMode)
    fun getCurrentDataMode(): Flow<DataMode>

    /**
     * Retrieve the current DataMode with a check on the user.
     * If the user is disconnected (or is an invalid user (empty)), the DataMode returned if Local.
     */
    fun getCurrentDataModeWithUserCheck(): Flow<DataMode>
}