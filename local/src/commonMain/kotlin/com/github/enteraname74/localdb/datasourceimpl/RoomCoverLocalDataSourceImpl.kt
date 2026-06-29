package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverLocalDataSource
import java.util.UUID

class RoomCoverLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
): CoverLocalDataSource {
    override suspend fun isCoverUsed(coverId: UUID): Boolean =
        appDatabase.coverDao.isCoverUsed(coverId = coverId)
}