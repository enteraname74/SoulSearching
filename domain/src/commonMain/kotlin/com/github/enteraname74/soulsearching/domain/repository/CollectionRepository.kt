package com.github.enteraname74.soulsearching.domain.repository

import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CollectionRepository {
    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?>

    suspend fun incrementNbPlayed(collectionId: Uuid)
}
