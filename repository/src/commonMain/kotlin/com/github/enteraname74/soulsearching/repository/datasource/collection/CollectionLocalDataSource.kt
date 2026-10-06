package com.github.enteraname74.soulsearching.repository.datasource.collection

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CollectionLocalDataSource {
    fun getAllPaged(): Flow<PagingData<CollectionPreview>>

    fun getAll(): Flow<List<CollectionPreview>>

    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?>

    fun getFromIds(collectionIds: List<Uuid>): Flow<List<CollectionWithMusics>>

    fun getAllFromQuickAccess(): Flow<List<CollectionPreview>>

    suspend fun create(name: String): Collection

    suspend fun upsertAll(collections: List<Collection>)

    suspend fun deleteAll(collectionIds: List<Uuid>)

    suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>)

    suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>)

    suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>)

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>>

    suspend fun incrementNbPlayed(collectionId: Uuid)

    suspend fun cleanAllCovers()

    suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid>
}
