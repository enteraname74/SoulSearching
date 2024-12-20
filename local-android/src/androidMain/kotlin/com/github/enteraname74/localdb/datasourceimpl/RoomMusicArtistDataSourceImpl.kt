package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import java.util.*

/**
 * Implementation of the MusicArtistDataSource with Room's DAO.
 */
internal class RoomMusicArtistDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicArtistDataSource {
    override suspend fun getAll(): List<MusicArtist> =
        appDatabase.musicArtistDao.getAll().map { it.toMusicArtist() }

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.upsertMusicIntoArtist(
            roomMusicArtist = musicArtist.toRoomMusicArtist()
        )
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        appDatabase.musicArtistDao.upsertAll(musicArtists.map { it.toRoomMusicArtist() })
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.delete(roomMusicArtist = musicArtist.toRoomMusicArtist())
    }
}