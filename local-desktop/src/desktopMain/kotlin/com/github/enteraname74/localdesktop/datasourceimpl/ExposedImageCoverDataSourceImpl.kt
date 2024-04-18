package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.localdesktop.dao.ImageCoverDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the ImageCoverDataSource with Exposed.
 */
internal class ExposedImageCoverDataSourceImpl(
    private val imageCoverDao: ImageCoverDao
): ImageCoverDataSource {
    override suspend fun insertImageCover(imageCover: ImageCover) =
        imageCoverDao.insertImageCover(imageCover = imageCover)

    override suspend fun deleteImageCover(imageCover: ImageCover) =
        imageCoverDao.deleteImageCover(imageCover = imageCover)

    override suspend fun deleteFromCoverId(coverId: UUID) =
        imageCoverDao.deleteFromCoverId(coverId = coverId)

    override suspend fun getCoverOfElement(coverId: UUID) =
        imageCoverDao.getCoverOfElement(coverId = coverId)

    override fun getAllCoversAsFlow() = imageCoverDao.getAllCoversAsFlow()
}