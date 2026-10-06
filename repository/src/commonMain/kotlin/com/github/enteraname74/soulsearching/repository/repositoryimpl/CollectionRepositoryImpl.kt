package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CollectionRepositoryImpl(
    private val collectionLocalDataSource: CollectionLocalDataSource,
) : CollectionRepository {
    override fun getAllPaged(): Flow<PagingData<CollectionPreview>> =
        collectionLocalDataSource.getAllPaged()

    override fun getAll(): Flow<List<CollectionPreview>> = collectionLocalDataSource.getAll()

    override fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        collectionLocalDataSource.getCollectionPreview(collectionId)

    override fun getElements(collectionId: Uuid): Flow<List<CollectionElementPreview>> =
        collectionLocalDataSource.getElements(collectionId)

    override fun getFromIds(collectionIds: List<Uuid>): Flow<List<CollectionWithMusics>> =
        collectionLocalDataSource.getFromIds(collectionIds)

    override fun getAllFromQuickAccess(): Flow<List<CollectionPreview>> =
        collectionLocalDataSource.getAllFromQuickAccess()

    override suspend fun create(name: String): Collection = collectionLocalDataSource.create(name)

    override suspend fun upsertAll(collections: List<Collection>) {
        collectionLocalDataSource.upsertAll(collections)
    }

    override suspend fun deleteAll(collectionIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        collectionLocalDataSource.deleteAll(collectionIds)
    }

    override suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>) {
        collectionLocalDataSource.addArtists(collectionIds, artistIds)
    }

    override suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>) {
        collectionLocalDataSource.addAlbums(collectionIds, albumIds)
    }

    override suspend fun removeArtist(collectionId: Uuid, artistId: Uuid): SoulResult<Unit> =
        SoulResult.runCatching { collectionLocalDataSource.removeArtist(collectionId, artistId) }

    override suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid): SoulResult<Unit> =
        SoulResult.runCatching { collectionLocalDataSource.removeAlbum(collectionId, albumId) }

    override fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>> =
        collectionLocalDataSource.getCollectionIdsContainingArtist(artistId)

    override fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>> =
        collectionLocalDataSource.getCollectionIdsContainingAlbum(albumId)

    override suspend fun incrementNbPlayed(collectionId: Uuid) {
        collectionLocalDataSource.incrementNbPlayed(collectionId)
    }

    override suspend fun cleanAllCovers() {
        collectionLocalDataSource.cleanAllCovers()
    }
}
