package com.github.enteraname74.soulsearching.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
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

    suspend fun create(name: String): Collection

    suspend fun upsertAll(collections: List<Collection>)

    suspend fun deleteAll(collectionIds: List<Uuid>): SoulResult<Unit>

    suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>)

    suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>)

    suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>)

    suspend fun removeArtist(collectionId: Uuid, artistId: Uuid): SoulResult<Unit>

    suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid): SoulResult<Unit>

    suspend fun removePlaylist(collectionId: Uuid, playlistId: Uuid): SoulResult<Unit>

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>>

    fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>>

    suspend fun incrementNbPlayed(collectionId: Uuid)

    suspend fun cleanAllCovers()
}
