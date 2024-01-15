package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.localdesktop.dao.ImageCoverDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the ImageCoverDao for Exposed.
 */
class ExposedImageCoverDaoImpl: ImageCoverDao {
    override suspend fun insertImageCover(imageCover: ImageCover) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteImageCover(imageCover: ImageCover) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFromCoverId(coverId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? {
        TODO("Not yet implemented")
    }

    override fun getAllCoversAsFlow(): Flow<List<ImageCover>> {
        TODO("Not yet implemented")
    }
}