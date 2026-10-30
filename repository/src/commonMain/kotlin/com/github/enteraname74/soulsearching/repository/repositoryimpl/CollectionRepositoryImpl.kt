package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.CloudCollection
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import com.github.enteraname74.soulsearching.domain.repository.CoverRepository
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.collection.CollectionRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CollectionRepositoryImpl(
    private val collectionLocalDataSource: CollectionLocalDataSource,
    private val collectionRemoteDataSource: CollectionRemoteDataSource,
    private val coverRepository: CoverRepository,
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

    override fun searchAll(search: String): Flow<List<CollectionPreview>> =
        collectionLocalDataSource.searchAll(search)

    override suspend fun create(name: String): Collection = collectionLocalDataSource.create(name)

    override suspend fun upsertAll(collections: List<Collection>, keepUpdatedAt: Boolean) {
        collectionLocalDataSource.upsertAll(collections, keepUpdatedAt)
    }

    override suspend fun getFromRemoteId(remoteId: Uuid): Collection? =
        collectionLocalDataSource.getFromRemoteId(remoteId)

    override suspend fun getFromName(name: String): Collection? =
        collectionLocalDataSource.getFromName(name)

    override suspend fun deleteAll(collectionIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        val remoteIds = collectionLocalDataSource.getRemoteIdsFromIds(collectionIds)
        collectionLocalDataSource.deleteAll(collectionIds)
        collectionRemoteDataSource.deleteAll(remoteIds)
    }

    override suspend fun addArtists(
        collectionIds: List<Uuid>,
        artistIds: List<Uuid>,
        keepUpdatedAt: Boolean,
    ) {
        collectionLocalDataSource.addArtists(collectionIds, artistIds, keepUpdatedAt)
    }

    override suspend fun addAlbums(
        collectionIds: List<Uuid>,
        albumIds: List<Uuid>,
        keepUpdatedAt: Boolean,
    ) {
        collectionLocalDataSource.addAlbums(collectionIds, albumIds, keepUpdatedAt)
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

    override suspend fun getAllToSendToCloud(): List<CollectionSyncData> =
        collectionLocalDataSource.getAllToSendToCloud()

    override suspend fun fetchUpdatedCollectionsFromCloud(lastSyncMillis: Long?): List<CloudCollection> {
        var page = 0
        val fetchedCollections = mutableListOf<CloudCollection>()
        while (true) {
            val fetchedData = collectionRemoteDataSource.getOfUser(
                lastUpdateAt = lastSyncMillis,
                maxPerPage = 1,
                page = page,
            )
            fetchedCollections += fetchedData
            if (fetchedData.isEmpty()) break
            page += 1
        }
        return fetchedCollections
    }

    override suspend fun uploadToCloud(collection: CollectionSyncData): CloudCollection =
        collectionRemoteDataSource.upload(
            collection = collection,
            coversData = collection.collection.cover.toUploadData(),
        )

    private suspend fun Cover?.toUploadData(): List<ByteArray?> = when (this) {
        null -> emptyList()
        is Cover.CoverFile -> listOf(coverRepository.getCoverData(this))
        is Cover.Url -> listOf(null)
        is Cover.Grid -> list.map { (it as? Cover.CoverFile)?.let { coverFile -> coverRepository.getCoverData(coverFile) } }
    }

    override suspend fun getDeletedRemoteCollectionIds(): List<Uuid> =
        collectionRemoteDataSource.getDeletedRemoteCollectionIds(
            collectionLocalDataSource.getAllRemoteIdsPossessedByUser()
        )

    override suspend fun deleteAllFromRemote(remoteIds: List<Uuid>) {
        collectionLocalDataSource.deleteAllFromRemote(remoteIds)
    }

    override suspend fun deleteAllRemoteFields() {
        collectionLocalDataSource.deleteAllRemoteFields()
    }

    override suspend fun deleteAllEmpty() {
        collectionLocalDataSource.deleteAllEmpty()
    }

    override suspend fun getLatestUpdatedAt(): Long? = collectionLocalDataSource.getLatestUpdatedAt()

    override suspend fun getLocalArtistIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid> =
        collectionLocalDataSource.getLocalArtistIdsFromRemoteIds(remoteIds)

    override suspend fun getLocalAlbumIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid> =
        collectionLocalDataSource.getLocalAlbumIdsFromRemoteIds(remoteIds)

    override fun getTotal(): Flow<Int> =
        collectionLocalDataSource.getTotal()
}
