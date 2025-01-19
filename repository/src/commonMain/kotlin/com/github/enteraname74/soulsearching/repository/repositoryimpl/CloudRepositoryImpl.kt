package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import kotlinx.coroutines.flow.Flow

class CloudRepositoryImpl(
    private val cloudLocalDataSource: CloudLocalDataSource,
): CloudRepository {
    override suspend fun clearLastUpdateDate() {
        cloudLocalDataSource.clearLastUpdateDate()
    }

    override suspend fun setSearchMetadata(searchMetadata: Boolean) {
        cloudLocalDataSource.setSearchMetadata(searchMetadata)
    }

    override suspend fun updateLastUpdateDate() {
        cloudLocalDataSource.updateLastUpdateDate()
    }

    override fun getSearchMetadata(): Flow<Boolean> =
        cloudLocalDataSource.getSearchMetadata()
}