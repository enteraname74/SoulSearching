package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the AlbumDataSource with Exposed.
 */
class ExposedAlbumDataSourceImpl : AlbumDataSource {
    override suspend fun insertAlbum(album: Album) {}

    override suspend fun deleteAlbum(album: Album) {
    }

    override suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> {
        return emptyList()
    }

    override suspend fun getAlbumFromId(albumId: UUID): Album? {
        return null
    }

    override fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> {
        return flowOf(null)
    }

    override suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics {
        return AlbumWithMusics()
    }

    override fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> {
        return emptyList()
    }

    override suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> {
        return emptyList()
    }

    override fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> {
        return flowOf(emptyList())
    }

    override suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? {
        return null
    }

    override suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? {
        return null
    }

    override suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) {

    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) {

    }

    override suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int {
        return 0
    }

    override suspend fun getNbPlayedOfAlbum(albumId: UUID): Int {
        return 0
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) {

    }
}