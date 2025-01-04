package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.repository.ReleaseRepository
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource

class ReleaseRepositoryImpl(
    private val releaseDataSource: ReleaseDataSource,
): ReleaseRepository {
    override suspend fun getLatestRelease(): Release? =
        releaseDataSource.getLatestRelease()

}