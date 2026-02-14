package com.github.enteraname74.domain.usecase.album

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class CommonAlbumUseCase(
    private val albumRepository: AlbumRepository
) {
    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumRepository.getAlbumWithMusics(
            albumId = albumId,
        )

    fun getAllPaged(): Flow<PagingData<AlbumPreview>> =
        albumRepository.getAllPaged()

    fun getAllFromQuickAccess(): Flow<List<AlbumPreview>> =
        albumRepository.getAllFromQuickAccess()

    fun getMostListened(): Flow<List<AlbumPreview>> =
        albumRepository.getMostListened()

    suspend fun deleteAll(albumsIds: List<UUID>) {
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

    fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> =
        albumRepository.getAlbumsOfArtist(
            artistId = artistId,
        )

    fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumRepository.getAlbumsWithMusicsOfArtist(
            artistId = artistId,
        )

    suspend fun incrementAlbumNbPlayed(albumId: UUID) {
        val album: Album = albumRepository.getFromId(albumId = albumId).first() ?: return
        albumRepository.upsert(
            album = album.copy(
                nbPlayed = album.nbPlayed + 1,
            )
        )
    }

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

}