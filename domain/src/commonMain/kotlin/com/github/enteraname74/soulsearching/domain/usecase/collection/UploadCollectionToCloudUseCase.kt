package com.github.enteraname74.soulsearching.domain.usecase.collection

import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.MergeMode
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository

class UploadCollectionToCloudUseCase(
    private val collectionRepository: CollectionRepository,
    private val upsertCloudCollectionUseCase: UpsertCloudCollectionUseCase,
) {
    suspend operator fun invoke(
        collection: CollectionSyncData,
        mergeMode: MergeMode,
    ): SoulResult<Unit> = SoulResult.runCatching {
        val cloudCollection = collectionRepository.uploadToCloud(collection)
        upsertCloudCollectionUseCase(listOf(cloudCollection), mergeMode)
    }
}
