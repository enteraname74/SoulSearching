package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

internal class RoomCollectionLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
) : CollectionLocalDataSource {
    override fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        appDatabase.collectionDao.getCollectionPreview(collectionId).map {
            it?.toCollectionPreview()
        }

    override suspend fun incrementNbPlayed(collectionId: Uuid) {
        appDatabase.collectionDao.incrementNbPlayed(
            collectionId = collectionId,
            lastUpdatedMillis = DateUtils.now(),
        )
    }

    override suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid> =
        appDatabase.collectionDao.getAllLocalToRemoteIds().associate {
            it.remoteId to it.localId
        }
}
