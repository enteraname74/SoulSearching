package com.github.enteraname74.soulsearching.domain.usecase.collection

import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CommonCollectionUseCase(
    private val collectionRepository: CollectionRepository,
) {
    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        collectionRepository.getCollectionPreview(collectionId)
}
