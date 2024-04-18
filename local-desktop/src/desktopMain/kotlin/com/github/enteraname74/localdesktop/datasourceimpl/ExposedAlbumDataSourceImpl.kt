package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.localdesktop.dao.AlbumDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the AlbumDataSource with Exposed.
 */
internal class ExposedAlbumDataSourceImpl(
    private val albumDao: AlbumDao
) : AlbumDataSource {
    override suspend fun insertAlbum(album: Album) = albumDao.insertAlbum(album = album)

    override suspend fun deleteAlbum(album: Album) = albumDao.deleteAlbum(album = album)

    override suspend fun getAllAlbumsFromArtist(artistId: UUID) =
        albumDao.getAllAlbumsFromArtist(
            artistId = artistId
        )

    override suspend fun getAlbumFromId(albumId: UUID) =
        albumDao.getAlbumFromId(albumId = albumId)

    override fun getAlbumWithMusicsAsFlow(albumId: UUID) = albumDao.getAlbumWithMusicsAsFlow(
        albumId = albumId
    )

    override suspend fun getAlbumWithMusics(albumId: UUID) = albumDao.getAlbumWithMusics(
        albumId = albumId
    )

    override fun getAllAlbumsSortByNameAscAsFlow() = albumDao.getAllAlbumsSortByNameAscAsFlow()

    override fun getAllAlbumsWithMusicsSortByNameAscAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByNameAscAsFlow()

    override fun getAllAlbumsWithMusicsSortByNameDescAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByNameDescAsFlow()

    override fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow()

    override fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow()

    override fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow()

    override fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow() =
        albumDao.getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow()

    override suspend fun getAllAlbumsWithArtist() =
        albumDao.getAllAlbumsWithArtist()

    override suspend fun getAllAlbumsWithMusics() =
        albumDao.getAllAlbumsWithMusics()

    override fun getAllAlbumWithArtistFromQuickAccessAsFlow() =
        albumDao.getAllAlbumWithArtistFromQuickAccessAsFlow()

    override suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID) =
        albumDao.getCorrespondingAlbum(
            albumName = albumName,
            artistId = artistId
        )

    override suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ) = albumDao.getPossibleDuplicateAlbum(
        albumId = albumId,
        albumName = albumName,
        artistId = artistId
    )

    override suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) =
        albumDao.updateAlbumCover(
            newCoverId = newCoverId,
            albumId = albumId
        )

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) =
        albumDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            albumId = albumId
        )

    override suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID) =
        albumDao.getNumberOfAlbumsWithCoverId(coverId = coverId)

    override suspend fun getNbPlayedOfAlbum(albumId: UUID) =
        albumDao.getNbPlayedOfAlbum(albumId = albumId)

    override suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) =
        albumDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            albumId = albumId
        )
}