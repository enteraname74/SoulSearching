package com.github.enteraname74.domain.usecase.album

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CommonAlbumUseCase(
    private val albumRepository: AlbumRepository
) {

    fun getFromId(albumId: Uuid): Flow<Album?> =
        albumRepository.getFromId(albumId)

    fun getFromIds(albumIds: List<Uuid>): Flow<List<AlbumWithMusics>> =
        albumRepository.getFromIds(albumIds)

    fun getAlbumWithMusics(albumId: Uuid): Flow<AlbumWithMusics?> =
        albumRepository.getAlbumWithMusics(
            albumId = albumId,
        )

    fun getAllPaged(): Flow<PagingData<AlbumPreview>> =
        albumRepository.getAllPaged()

    suspend fun getAll(page: Int, pageSize: Int): List<AlbumPreview> =
        albumRepository.getAll(page = page, pageSize = pageSize)

    fun getAllFromQuickAccess(): Flow<List<AlbumPreview>> =
        albumRepository.getAllFromQuickAccess()

    fun getAlbumPreview(albumId: Uuid): Flow<AlbumPreview?> =
        albumRepository.getAlbumPreview(albumId)

    fun searchAll(search: String): Flow<List<AlbumPreview>> =
        albumRepository.searchAll(search)

    /**
     * Call [DeleteAlbumUseCase] for a clean delete of related songs and artists if needed
     */
    suspend fun deleteAll(albumsIds: List<Uuid>) {
        albumRepository.deleteAll(
            ids = albumsIds,
        )
    }

    suspend fun getAlbumsNameFromSearch(searchString: String): List<String> =
        if (searchString.isBlank()) {
            emptyList()
        } else {
            albumRepository.getAlbumNamesContainingSearch(searchString)
        }

    fun getAlbumsOfArtist(artistId: Uuid): Flow<List<Album>> =
        albumRepository.getAlbumsOfArtist(
            artistId = artistId,
        )

    fun getAlbumsWithMusicsOfArtist(artistId: Uuid): Flow<List<AlbumWithMusics>> =
        albumRepository.getAlbumsWithMusicsOfArtist(
            artistId = artistId,
        )

    suspend fun upsert(album: Album) {
        albumRepository.upsert(
            album = album,
        )
    }

    suspend fun upsertAll(albums: List<Album>) {
        albumRepository.upsertAll(albums)
    }

    suspend fun cleanAllCovers() {
        albumRepository.cleanAllCovers()
    }

    suspend fun getFromInformation(
        albumName: String,
        artistName: String,
    ): Album? =
        albumRepository.getFromInformation(
            albumName = albumName,
            artistName = artistName,
        )
}
