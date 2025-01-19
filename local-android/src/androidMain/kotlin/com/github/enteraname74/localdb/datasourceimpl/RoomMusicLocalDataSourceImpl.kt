package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusic
import com.github.enteraname74.localdb.model.toRoomMusic
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the MusicDataSource with Room's DAO.
 */
internal class RoomMusicLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
) : MusicLocalDataSource {
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

    override suspend fun deleteAll(ids: List<UUID>) {
        appDatabase.musicDao.deleteAll(
            ids = ids,
        )
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        appDatabase.musicDao.deleteAll(
            dataMode = dataMode.value,
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

    override fun getAll(dataMode: DataMode): Flow<List<Music>> {
        return appDatabase.musicDao.getAll(dataMode.value).map { list ->
            list.map { it.toMusic() }
        }
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        appDatabase.musicDao.getAllMusicFromAlbum(
            albumId = albumId
        ).map { it.toMusic() }

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        appDatabase.musicDao.updateMusicsAlbum(
            newAlbumId, legacyAlbumId
        )
    }
}