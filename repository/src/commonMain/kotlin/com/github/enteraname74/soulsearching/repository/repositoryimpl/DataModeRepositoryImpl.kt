package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.DataModeRepository
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import kotlinx.coroutines.flow.Flow

class DataModeRepositoryImpl(
    private val dataModeDataSource: DataModeDataSource
): DataModeRepository {
    override suspend fun switchDataMode(newDataMode: DataMode) {
        dataModeDataSource.switchDataMode(newDataMode)
    }

    override fun getCurrentDataMode(): Flow<DataMode> =
        dataModeDataSource.getCurrentDataMode()
}