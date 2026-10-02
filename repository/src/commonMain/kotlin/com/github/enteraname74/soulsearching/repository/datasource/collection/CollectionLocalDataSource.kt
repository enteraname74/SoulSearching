package com.github.enteraname74.soulsearching.repository.datasource.collection

import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CollectionLocalDataSource {
    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?>

    suspend fun incrementNbPlayed(collectionId: Uuid)

    suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid>
}
