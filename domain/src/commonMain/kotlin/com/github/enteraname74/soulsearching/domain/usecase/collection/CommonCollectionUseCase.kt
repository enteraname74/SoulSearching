package com.github.enteraname74.soulsearching.domain.usecase.collection

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Collection
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
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

    suspend fun create(name: String): Collection = collectionRepository.create(name.trim())

    suspend fun addArtists(collectionIds: List<Uuid>, artistIds: List<Uuid>) {
        collectionRepository.addArtists(collectionIds, artistIds)
    }

    suspend fun addAlbums(collectionIds: List<Uuid>, albumIds: List<Uuid>) {
        collectionRepository.addAlbums(collectionIds, albumIds)
    }

    suspend fun addPlaylists(collectionIds: List<Uuid>, playlistIds: List<Uuid>) {
        collectionRepository.addPlaylists(collectionIds, playlistIds)
    }

    fun getCollectionIdsContainingArtist(artistId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingArtist(artistId)

    fun getCollectionIdsContainingAlbum(albumId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingAlbum(albumId)

    fun getCollectionIdsContainingPlaylist(playlistId: Uuid): Flow<List<Uuid>> =
        collectionRepository.getCollectionIdsContainingPlaylist(playlistId)
}
