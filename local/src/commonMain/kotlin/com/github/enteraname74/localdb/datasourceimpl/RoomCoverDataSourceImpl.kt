package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.soulsearching.repository.datasource.CoverDataSource
import java.util.UUID

class RoomCoverDataSourceImpl(
    private val appDatabase: AppDatabase,
): CoverDataSource {
    override suspend fun isCoverUsed(coverId: UUID): Boolean =
        appDatabase.coverDao.isCoverUsed(coverId = coverId)
}