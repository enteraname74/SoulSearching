package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.model.ImageCover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ExposedImageCoverDataSourceImpl: ImageCoverDataSource {
    override suspend fun insertImageCover(imageCover: ImageCover) {

    }

    override suspend fun deleteImageCover(imageCover: ImageCover) {

    }

    override suspend fun deleteFromCoverId(coverId: UUID) {

    }

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? {
        return null
    }

    override fun getAllCoversAsFlow(): Flow<List<ImageCover>> {
        return flowOf(emptyList())
    }
}