package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CollectionRepositoryImpl(
    private val collectionLocalDataSource: CollectionLocalDataSource,
) : CollectionRepository {
    override fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        collectionLocalDataSource.getCollectionPreview(collectionId)

    override suspend fun incrementNbPlayed(collectionId: Uuid) {
        collectionLocalDataSource.incrementNbPlayed(collectionId)
    }
}
