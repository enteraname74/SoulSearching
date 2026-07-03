package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import kotlin.uuid.Uuid

/**
 * Implementation of the MusicArtistDataSource with Room's DAO.
 */
internal class RoomMusicArtistDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicArtistDataSource {
    override suspend fun get(artistId: Uuid, musicId: Uuid): MusicArtist? =
        appDatabase.musicArtistDao.get(
            artistId = artistId,
            musicId = musicId,
        )?.toMusicArtist()

    override suspend fun upsertMusicIntoArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.upsertMusicIntoArtist(
            roomMusicArtist = musicArtist.toRoomMusicArtist()
        )
    }

    override suspend fun upsertAll(musicArtists: List<MusicArtist>) {
        appDatabase.musicArtistDao.upsertAll(musicArtists.map { it.toRoomMusicArtist() })
    }

    override suspend fun deleteMusicArtist(musicArtist: MusicArtist) {
        appDatabase.musicArtistDao.delete(id = musicArtist.id)
    }

    override suspend fun deleteOfArtist(artistId: Uuid) {
        appDatabase.musicArtistDao.deleteOfArtist(artistId)
    }

    override suspend fun deleteOfMusic(musicId: Uuid) {
        appDatabase.musicArtistDao.deleteOfMusic(musicId)
    }
}
