package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.soulsearching.repository.datasource.ImageCoverDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an ImageCover.
 */
class ImageCoverRepositoryImpl(
    private val imageCoverDataSource: ImageCoverDataSource
): ImageCoverRepository {
    override suspend fun upsert(imageCover: ImageCover) = imageCoverDataSource.upsert(
        imageCover = imageCover
    )

    override suspend fun delete(imageCover: ImageCover) = imageCoverDataSource.delete(
        imageCover = imageCover
    )

    override suspend fun delete(coverId: UUID) = imageCoverDataSource.deleteFromCoverId(
        coverId = coverId
    )

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? =
        imageCoverDataSource.getCoverOfElement(
            coverId = coverId
        )

    override fun getAll(): Flow<List<ImageCover>> = imageCoverDataSource.getAll()
}