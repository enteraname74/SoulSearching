package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusic
import com.github.enteraname74.localdb.model.toRoomMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the MusicDataSource with Room's DAO.
 */
internal class RoomMusicDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicDataSource {
    override suspend fun upsert(music: Music) {
        appDatabase.musicDao.upsert(
            roomMusic = music.toRoomMusic()
        )
    }

    override suspend fun upsertAll(musics: List<Music>) {
        appDatabase.musicDao.upsertAll(musics.map { it.toRoomMusic() })
    }

    override suspend fun delete(music: Music) {
        appDatabase.musicDao.delete(
            roomMusic = music.toRoomMusic()
        )
    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        appDatabase.musicDao.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )
    }

    override suspend fun getFromPath(musicPath: String): Music? {
        return appDatabase.musicDao.getMusicFromPath(
            musicPath = musicPath
        )?.toMusic()
    }

    override fun getFromId(musicId: UUID): Flow<Music?> {
        return appDatabase.musicDao.getFromId(
            musicId = musicId
        ).map { it?.toMusic() }
    }

    override fun getAll(): Flow<List<Music>> {
        return appDatabase.musicDao.getAll().map { list ->
            list.map { it.toMusic() }
        }
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }
}