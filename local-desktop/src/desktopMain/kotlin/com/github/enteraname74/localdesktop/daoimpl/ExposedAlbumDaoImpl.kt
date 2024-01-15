package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.localdesktop.dao.AlbumDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the AlbumDao for Exposed.
 */
class ExposedAlbumDaoImpl: AlbumDao {
    override suspend fun insertAlbum(album: Album) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlbum(album: Album) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumFromId(albumId: UUID): Album? {
        TODO("Not yet implemented")
    }

    override fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? {
        TODO("Not yet implemented")
    }

    override suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getNbPlayedOfAlbum(albumId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) {
        TODO("Not yet implemented")
    }
}