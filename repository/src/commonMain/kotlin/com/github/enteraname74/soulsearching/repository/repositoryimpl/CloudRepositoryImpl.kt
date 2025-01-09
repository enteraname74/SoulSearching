package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource

class CloudRepositoryImpl(
    private val cloudLocalDataSource: CloudLocalDataSource,
): CloudRepository {
    override suspend fun clearLastUpdateDate() {
        cloudLocalDataSource.clearLastUpdateDate()
    }
}