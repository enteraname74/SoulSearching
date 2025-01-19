package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import java.util.*

/**
 * Implementation of the MusicArtistDataSource with Room's DAO.
 */
internal class RoomMusicArtistLocalDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicArtistLocalDataSource {
    override suspend fun getAll(dataMode: DataMode): List<MusicArtist> =
        appDatabase.musicArtistDao.getAll(dataMode.value).map { it.toMusicArtist() }

    override suspend fun get(artistId: UUID, musicId: UUID): MusicArtist? =
        appDatabase.musicArtistDao.get(
            artistId = artistId,
            musicId = musicId,
        )?.toMusicArtist()

    override suspend fun deleteAll(dataMode: DataMode) {
        appDatabase.musicArtistDao.deleteAll(dataMode.value)
    }

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.upsertMusicIntoArtist(
            roomMusicArtist = musicArtist.toRoomMusicArtist()
        )
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        appDatabase.musicArtistDao.upsertAll(musicArtists.map { it.toRoomMusicArtist() })
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.delete(
            musicId = musicArtist.musicId,
            artistId = musicArtist.artistId,
        )
    }
}