package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import java.util.*

/**
 * Implementation of the MusicArtistDataSource with Room's DAO.
 */
internal class RoomMusicArtistDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicArtistDataSource {
    override suspend fun insertMusicIntoArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.insertMusicIntoArtist(
            roomMusicArtist = musicArtist.toRoomMusicArtist()
        )
    }

    override suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID) {
        appDatabase.musicArtistDao.updateArtistOfMusic(
            musicId = musicId,
            newArtistId = newArtistId
        )
    }

    override suspend fun deleteMusicFromArtist(musicId: UUID) {
        appDatabase.musicArtistDao.deleteMusicFromArtist(
            musicId = musicId
        )
    }

    override suspend fun getArtistIdFromMusicId(musicId: UUID): UUID? {
        return appDatabase.musicArtistDao.getArtistIdFromMusicId(
            musicId = musicId
        )
    }
}