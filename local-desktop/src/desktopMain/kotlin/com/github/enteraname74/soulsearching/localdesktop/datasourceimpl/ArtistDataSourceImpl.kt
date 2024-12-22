package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.soulsearching.localdesktop.dao.ArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

internal class ArtistDataSourceImpl(
    private val artistDao: ArtistDao
) : ArtistDataSource {
    override suspend fun upsert(artist: Artist) {
        artistDao.upsert(artist)
    }

    override suspend fun upsertAll(artists: List<Artist>) {
        artistDao.upsertAll(
            artists = artists,
        )
    }

    override suspend fun deleteAll(artist: Artist) {
        artistDao.delete(artist)
    }

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        artistDao.deleteAll(artistsIds)
    }

    override fun getFromId(artistId: UUID): Flow<Artist?> =
        artistDao.getFromId(artistId)

    override fun getAll(): Flow<List<Artist>> =
        artistDao.getAll()

    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> =
        artistDao.getAllArtistWithMusics()

    override suspend fun getFromName(artistName: String): Artist? =
        artistDao.getFromName(artistName)

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistDao.getAllFromNames(artistsNames)

    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDao.getArtistWithMusics(artistId)

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistDao.getArtistsOfMusic(musicId)
}