package com.github.enteraname74.soulsearching.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.CloudCollection
import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CollectionRepository {
    fun getAllPaged(): Flow<PagingData<CollectionPreview>>

    fun getAll(): Flow<List<CollectionPreview>>

    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?>

    fun getElements(collectionId: Uuid): Flow<List<CollectionElementPreview>>

    fun getFromIds(collectionIds: List<Uuid>): Flow<List<CollectionWithMusics>>

    fun getAllFromQuickAccess(): Flow<List<CollectionPreview>>

    fun searchAll(search: String): Flow<List<CollectionPreview>>

    suspend fun create(name: String): Collection

    suspend fun upsertAll(collections: List<Collection>, keepUpdatedAt: Boolean = false)

    suspend fun getFromRemoteId(remoteId: Uuid): Collection?

    suspend fun getFromName(name: String): Collection?

    suspend fun deleteAll(collectionIds: List<Uuid>): SoulResult<Unit>

    suspend fun addArtists(
        collectionIds: List<Uuid>,
        artistIds: List<Uuid>,
        keepUpdatedAt: Boolean = false,
    )

    suspend fun addAlbums(
        collectionIds: List<Uuid>,
        albumIds: List<Uuid>,
        keepUpdatedAt: Boolean = false,
    )

    suspend fun removeArtist(collectionId: Uuid, artistId: Uuid): SoulResult<Unit>

    suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid): SoulResult<Unit>

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>>

    suspend fun incrementNbPlayed(collectionId: Uuid)

    suspend fun cleanAllCovers()

    suspend fun getAllToSendToCloud(): List<CollectionSyncData>

    suspend fun fetchUpdatedCollectionsFromCloud(lastSyncMillis: Long?): List<CloudCollection>

    suspend fun uploadToCloud(collection: CollectionSyncData): CloudCollection

    suspend fun getDeletedRemoteCollectionIds(): List<Uuid>

    suspend fun deleteAllFromRemote(remoteIds: List<Uuid>)

    suspend fun deleteAllRemoteFields()

    suspend fun deleteAllEmpty()

    suspend fun getLatestUpdatedAt(): Long?

    suspend fun getLocalArtistIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid>

    suspend fun getLocalAlbumIdsFromRemoteIds(remoteIds: List<Uuid>): List<Uuid>

    /**
     * Retrieves a flow of the total of collections shown in the library.
     */
    fun getTotal(): Flow<Int>
}
