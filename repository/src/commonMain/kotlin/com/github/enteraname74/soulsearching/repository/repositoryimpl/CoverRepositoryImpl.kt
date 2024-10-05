package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.CoverRepository
import com.github.enteraname74.domain.util.CoverFileManager
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

    override suspend fun delete(cover: Cover) {
        (cover as? Cover.FileCover)?.fileCoverId?.let { coverId ->
            coverFileManager.deleteFromId(
                id = coverId,
            )
        }
    }

    override suspend fun delete(coverId: UUID) {
        coverFileManager.deleteFromId(
            id = coverId,
        )
    }
}