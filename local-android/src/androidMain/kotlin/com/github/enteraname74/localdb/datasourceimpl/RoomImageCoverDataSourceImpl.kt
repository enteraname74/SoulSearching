package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toImageCover
import com.github.enteraname74.localdb.model.toRoomImageCover
import com.github.enteraname74.soulsearching.repository.datasource.ImageCoverDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the ImageCoverDataSource with Room's DAO.
 */
internal class RoomImageCoverDataSourceImpl(
    private val appDatabase: AppDatabase
) : ImageCoverDataSource {
    override suspend fun upsert(imageCover: ImageCover) {
        appDatabase.imageCoverDao.upsert(
            roomImageCover = imageCover.toRoomImageCover()
        )
    }

    override suspend fun delete(imageCover: ImageCover) {
        appDatabase.imageCoverDao.delete(
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

    override fun getAll(): Flow<List<ImageCover>> {
        return appDatabase.imageCoverDao.getAll().map { list ->
            list.map { it.toImageCover() }
        }
    }
}