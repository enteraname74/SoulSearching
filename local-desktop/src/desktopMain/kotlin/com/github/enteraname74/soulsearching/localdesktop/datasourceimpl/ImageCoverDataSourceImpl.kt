package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.soulsearching.localdesktop.dao.ImageCoverDao
import kotlinx.coroutines.flow.Flow
import java.util.*

internal class ImageCoverDataSourceImpl(
    private val imageCoverDao: ImageCoverDao
): ImageCoverDataSource {
    override suspend fun upsert(imageCover: ImageCover) {
        imageCoverDao.upsert(imageCover)
    }

    override suspend fun delete(imageCover: ImageCover) {
        imageCoverDao.delete(imageCover)
    }

    override fun getAll(): Flow<List<ImageCover>> =
        imageCoverDao.getAll()

    override suspend fun deleteFromCoverId(coverId: UUID) {
        imageCoverDao.deleteFromCoverId(coverId)
    }

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? =
        imageCoverDao.getCoverOfElement(coverId)
}