package com.github.enteraname74.soulsearching.domain.usecase.collection

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.CollectionElementPreview
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CommonCollectionUseCase(
    private val collectionRepository: CollectionRepository,
) {
    fun getAllPaged(): Flow<PagingData<CollectionPreview>> = collectionRepository.getAllPaged()

    fun getAll(): Flow<List<CollectionPreview>> = collectionRepository.getAll()

    fun getCollectionPreview(collectionId: Uuid): Flow<CollectionPreview?> =
        collectionRepository.getCollectionPreview(collectionId)

    fun getElements(collectionId: Uuid): Flow<List<CollectionElementPreview>> =
        collectionRepository.getElements(collectionId)

    fun getFromIds(collectionIds: List<Uuid>): Flow<List<CollectionWithMusics>> =
        collectionRepository.getFromIds(collectionIds)

    fun getAllFromQuickAccess(): Flow<List<CollectionPreview>> =
        collectionRepository.getAllFromQuickAccess()

    suspend fun create(name: String): Collection = collectionRepository.create(name.trim())

    suspend fun upsertAll(collections: List<Collection>) {
        collectionRepository.upsertAll(collections)
    }

    suspend fun upsert(collection: Collection) {
        collectionRepository.upsertAll(listOf(collection))
    }

    suspend fun deleteAll(collectionIds: List<Uuid>): SoulResult<Unit> =
        collectionRepository.deleteAll(collectionIds)

    suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>) {
        collectionRepository.addArtists(collectionIds, artistIds)
    }

    suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>) {
        collectionRepository.addAlbums(collectionIds, albumIds)
    }

    suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>) {
        collectionRepository.addPlaylists(collectionIds, playlistIds)
    }

    suspend fun removeArtist(collectionId: Uuid, artistId: Uuid): SoulResult<Unit> =
        collectionRepository.removeArtist(collectionId, artistId)

    suspend fun removeAlbum(collectionId: Uuid, albumId: Uuid): SoulResult<Unit> =
        collectionRepository.removeAlbum(collectionId, albumId)

    suspend fun removePlaylist(collectionId: Uuid, playlistId: Uuid): SoulResult<Unit> =
        collectionRepository.removePlaylist(collectionId, playlistId)

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingArtist(artistId)

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingAlbum(albumId)

    fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingPlaylist(playlistId)

    suspend fun cleanAllCovers() {
        collectionRepository.cleanAllCovers()
    }
}
