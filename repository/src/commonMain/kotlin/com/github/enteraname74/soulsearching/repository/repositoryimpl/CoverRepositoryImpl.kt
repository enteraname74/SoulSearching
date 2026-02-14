package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.repository.CoverRepository
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.repository.datasource.CoverDataSource
import java.util.*

/**
 * Repository of an ImageCover.
 */
class CoverRepositoryImpl(
    private val coverFileManager: CoverFileManager,
    private val coverDataSource: CoverDataSource,
): CoverRepository {
    override suspend fun upsert(id: UUID, data: ByteArray) {
        coverFileManager.saveCover(
            id = id,
            data = data,
        )
    }

    override suspend fun getAllCoverIds(): List<UUID> =
        coverFileManager.getAllCoverIds()

    override suspend fun delete(coverId: UUID) {
        coverFileManager.deleteFromId(
            id = coverId,
        )
    }

    override suspend fun isCoverUsed(coverId: UUID): Boolean =
        coverDataSource.isCoverUsed(coverId)
}