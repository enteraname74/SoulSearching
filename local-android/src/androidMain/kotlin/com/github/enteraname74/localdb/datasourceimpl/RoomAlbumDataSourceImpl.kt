package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toAlbum
import com.github.enteraname74.localdb.model.toAlbumWithArtist
import com.github.enteraname74.localdb.model.toAlbumWithMusics
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the AlbumDataSource with Room's DAO.
 */
internal class RoomAlbumDataSourceImpl(
    private val appDatabase: AppDatabase
) : AlbumDataSource {
    override suspend fun insertAlbum(album: Album) {
        appDatabase.albumDao.insertAlbum(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun deleteAlbum(album: Album) {
        appDatabase.albumDao.deleteAlbum(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> {
        return appDatabase.albumDao.getAllAlbumsFromArtist(
            artistId = artistId
        ).map { it.toAlbum() }
    }

    override suspend fun getAlbumFromId(albumId: UUID): Album? {
        return appDatabase.albumDao.getAlbumFromId(
            albumId = albumId
        )?.toAlbum()
    }

    override fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> {
        return appDatabase.albumDao.getAlbumWithMusicsAsFlow(
            albumId = albumId
        ).map { it?.toAlbumWithMusics() }
    }

    override suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics {
        return appDatabase.albumDao.getAlbumWithMusics(
            albumId = albumId
        ).toAlbumWithMusics()
    }

    override fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> {
        return appDatabase.albumDao.getAllAlbumsSortByNameAscAsFlow().map { list ->
            list.map { it.toAlbum() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByNameAscAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByNameDescAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> {
        return appDatabase.albumDao.getAllAlbumsWithArtist().map { it.toAlbumWithArtist() }
    }

    override suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> {
        return appDatabase.albumDao.getAllAlbumsWithMusics().map { it.toAlbumWithMusics() }
    }

    override fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> {
        return appDatabase.albumDao.getAllAlbumWithArtistFromQuickAccessAsFlow().map { list ->
            list.map { it.toAlbumWithArtist() }
        }
    }

    override suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? {
        return appDatabase.albumDao.getCorrespondingAlbum(
            albumName = albumName,
            artistId = artistId
        )?.toAlbum()
    }

    override suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? {
        return appDatabase.albumDao.getPossibleDuplicateAlbum(
            albumId = albumId,
            albumName = albumName,
            artistId = artistId
        )?.toAlbum()
    }

    override suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) {
        appDatabase.albumDao.updateAlbumCover(
            newCoverId = newCoverId,
            albumId = albumId
        )
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) {
        appDatabase.albumDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            albumId = albumId
        )
    }

    override suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int {
        return appDatabase.albumDao.getNumberOfAlbumsWithCoverId(
            coverId = coverId
        )
    }

    override suspend fun getNbPlayedOfAlbum(albumId: UUID): Int {
        return appDatabase.albumDao.getNbPlayedOfAlbum(
            albumId = albumId
        ) ?: 0
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) {
        appDatabase.albumDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            albumId = albumId
        )
    }
}