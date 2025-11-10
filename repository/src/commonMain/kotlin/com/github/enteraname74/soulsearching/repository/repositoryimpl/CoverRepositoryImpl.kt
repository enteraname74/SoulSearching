package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.repository.CoverRepository
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import java.util.*

/**
 * Repository of an ImageCover.
 */
class CoverRepositoryImpl(
    private val coverFileManager: CoverFileManager,
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
}