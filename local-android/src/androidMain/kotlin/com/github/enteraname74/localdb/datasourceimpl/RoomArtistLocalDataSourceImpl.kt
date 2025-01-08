package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toArtist
import com.github.enteraname74.localdb.model.toArtistWithMusics
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the ArtistDataSource with Room's DAO.
 */
internal class RoomArtistLocalDataSourceImpl(
    private val appDatabase: AppDatabase
) : ArtistLocalDataSource {
    override suspend fun upsert(artist: Artist) {
        appDatabase.artistDao.upsert(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun upsertAll(artists: List<Artist>) {
        appDatabase.artistDao.upsertAll(artists.map { it.toRoomArtist() })
    }

    override suspend fun deleteAll(artist: Artist) {
        appDatabase.artistDao.delete(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        appDatabase.artistDao.deleteAll(
            ids = artistsIds,
        )
    }

    override fun getFromId(artistId: UUID): Flow<Artist?> {
        return appDatabase.artistDao.getFromId(
            artistId = artistId
        ).map { it?.toArtist() }
    }

    override fun getAll(): Flow<List<Artist>> {
        return appDatabase.artistDao.getAll().map { list ->
            list.map { it.toArtist() }
        }
    }

    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusics().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override suspend fun getFromName(artistName: String): Artist? {
        return appDatabase.artistDao.getFromName(
            artistName = artistName
        )?.toArtist()
    }

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        appDatabase.artistDao.getAllFromName(
            artistsNames
        ).map { it.toArtist() }

    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> {
        return appDatabase.artistDao.getArtistWithMusics(
            artistId = artistId
        ).map { it?.toArtistWithMusics() }
    }

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        appDatabase.artistDao.getArtistsOfMusic(musicId).map { list ->
            list.map { it.toArtist() }
        }
}