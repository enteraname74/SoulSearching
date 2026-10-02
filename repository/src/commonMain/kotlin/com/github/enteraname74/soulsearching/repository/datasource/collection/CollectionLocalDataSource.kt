package com.github.enteraname74.soulsearching.repository.datasource.collection

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CollectionLocalDataSource {
    fun getAllPaged(): Flow<PagingData<CollectionPreview>>

    fun getAll(): Flow<List<CollectionPreview>>

    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?>

    suspend fun create(name: String): Collection

    suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>)

    suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>)

    suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>)

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>>

    suspend fun incrementNbPlayed(collectionId: Uuid)

    suspend fun getAllRemoteToLocalIds(): Map<Uuid, Uuid>
}
