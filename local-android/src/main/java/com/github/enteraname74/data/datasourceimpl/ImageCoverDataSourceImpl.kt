package com.github.enteraname74.data.datasourceimpl

import com.github.enteraname74.data.AppDatabase
import com.github.enteraname74.data.model.toImageCover
import com.github.enteraname74.data.model.toRoomImageCover
import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.model.ImageCover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of the ImageCoverDataSource with Room's DAO.
 */
internal class ImageCoverDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : ImageCoverDataSource {
    override suspend fun insertImageCover(imageCover: ImageCover) {
        appDatabase.imageCoverDao.insertImageCover(
            roomImageCover = imageCover.toRoomImageCover()
        )
    }

    override suspend fun deleteImageCover(imageCover: ImageCover) {
        appDatabase.imageCoverDao.deleteImageCover(
            roomImageCover = imageCover.toRoomImageCover()
        )
    }

    override suspend fun deleteFromCoverId(coverId: UUID) {
        appDatabase.imageCoverDao.deleteFromCoverId(
            coverId = coverId
        )
    }

    override suspend fun getCoverOfElement(coverId: UUID): ImageCover? {
        return appDatabase.imageCoverDao.getCoverOfElement(
            coverId = coverId
        )?.toImageCover()
    }

    override fun getAllCoversAsFlow(): Flow<List<ImageCover>> {
        return appDatabase.imageCoverDao.getAllCoversAsFlow().map { list ->
            list.map { it.toImageCover() }
        }
    }
}