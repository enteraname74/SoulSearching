package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.DataMode
import kotlinx.coroutines.flow.Flow

interface DataModeRepository {
    suspend fun switchDataMode(newDataMode: DataMode)
    fun getCurrentDataMode(): Flow<DataMode>
}