package com.github.enteraname74.localandroid.datasourceimpl

import com.github.enteraname74.datasource.ImageCoverDataSource
import com.github.enteraname74.localandroid.AppDatabase
import com.github.enteraname74.localandroid.model.toImageCover
import com.github.enteraname74.localandroid.model.toRoomImageCover
import com.github.enteraname74.model.ImageCover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the ImageCoverDataSource with Room's DAO.
 */
internal class ImageCoverDataSourceImpl(
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