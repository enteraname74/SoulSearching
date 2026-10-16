package com.github.enteraname74.soulsearching.domain.usecase.collection

import com.github.enteraname74.soulsearching.domain.model.CloudCollection
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.MergeMode
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository

class UpsertCloudCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val commonCollectionUseCase: CommonCollectionUseCase,
) {
    suspend operator fun invoke(
        cloudCollections: List<CloudCollection>,
        mergeMode: MergeMode,
    ) {
        val collectionsToSave = cloudCollections.map { cloudCollection ->
            val existing = collectionRepository.getFromRemoteId(cloudCollection.collection.id)
                ?: collectionRepository.getFromName(cloudCollection.collection.name)
            existing?.merge(cloudCollection.collection, mergeMode) ?: cloudCollection.toNewCollection()
        }
        commonCollectionUseCase.upsertAll(collectionsToSave, keepUpdatedAt = true)

        collectionsToSave.forEach { collection ->
            val cloudCollection = cloudCollections.find { it.collection.id == collection.remoteId } ?: return@forEach
            val artistIds = collectionRepository.getLocalArtistIdsFromRemoteIds(cloudCollection.artistIds)
            val albumIds = collectionRepository.getLocalAlbumIdsFromRemoteIds(cloudCollection.albumIds)
            commonCollectionUseCase.addArtists(
                listOf(collection.collectionId),
                artistIds,
                keepUpdatedAt = true,
            )
            commonCollectionUseCase.addAlbums(
                listOf(collection.collectionId),
                albumIds,
                keepUpdatedAt = true,
            )
        }
    }
}
