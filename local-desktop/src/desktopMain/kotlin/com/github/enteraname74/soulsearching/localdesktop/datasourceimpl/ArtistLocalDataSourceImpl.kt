package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.soulsearching.localdesktop.dao.ArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

internal class ArtistLocalDataSourceImpl(
    private val artistDao: ArtistDao
) : ArtistLocalDataSource {
    override suspend fun upsert(artist: Artist) {
        artistDao.upsert(artist)
    }

    override suspend fun upsertAll(artists: List<Artist>) {
        artistDao.upsertAll(
            artists = artists,
        )
    }

    override suspend fun delete(artist: Artist) {
        artistDao.delete(artist)
    }

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        artistDao.deleteAll(artistsIds)
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        artistDao.deleteAll(dataMode.value)
    }

    override fun getAll(dataMode: DataMode): Flow<List<Artist>> =
        artistDao.getAll(dataMode.value)

    override suspend fun getAll(artistIds: List<UUID>): List<Artist> =
        artistDao.getAll(artistIds)

    override suspend fun getArtistNamesContainingSearch(search: String): List<String> =
        artistDao.getArtistNamesContainingSearch(search)

    override fun getFromId(artistId: UUID): Flow<Artist?> =
        artistDao.getFromId(artistId)

    override fun getAllArtistWithMusics(dataMode: DataMode): Flow<List<ArtistWithMusics>> =
        artistDao.getAllArtistWithMusics(dataMode = dataMode.value)

    override suspend fun getFromName(artistName: String): Artist? =
        artistDao.getFromName(artistName)

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistDao.getAllFromNames(artistsNames)

    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDao.getArtistWithMusics(artistId)

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistDao.getArtistsOfMusic(musicId)
}