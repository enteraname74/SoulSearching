package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class CommonAlbumUseCase(
    private val albumRepository: AlbumRepository
) {
    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumRepository.getAlbumWithMusics(
            albumId = albumId,
        )

    fun getAll(): Flow<List<Album>> =
        albumRepository.getAll()

    fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> =
        albumRepository.getAllAlbumsWithArtist()

    fun getAllAlbumsWithMusics(): Flow<List<AlbumWithMusics>> =
        albumRepository.getAllAlbumWithMusics()

    fun getAllFromQuickAccess(): Flow<List<AlbumWithMusics>> =
        albumRepository.getAllAlbumWithMusics().map { list ->
            list.filter { it.album.isInQuickAccess }
        }

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

    suspend fun getDuplicatedAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? {
        val allAlbumsWithArtists: List<AlbumWithArtist> = albumRepository.getAllAlbumsWithArtist().first()
        return allAlbumsWithArtists
            .firstOrNull {
                it.album.albumName == albumName &&
                        it.artist?.artistId == artistId &&
                        it.album.albumId != albumId
            }?.album
    }

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
}