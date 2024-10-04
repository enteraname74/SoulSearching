package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicDao
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

internal class MusicDataSourceImpl(
    private val musicDao: MusicDao
): MusicDataSource {
    override suspend fun upsert(music: Music) {
        musicDao.upsert(music)
    }

    override suspend fun upsertAll(musics: List<Music>) {
        musicDao.upsertAll(
            musics = musics,
        )
    }

    override suspend fun delete(music: Music) {
        musicDao.delete(music)
    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        musicDao.deleteMusicFromAlbum(album, artist)
    }

    override suspend fun getFromPath(musicPath: String): Music? =
        musicDao.getFromPath(musicPath)

    override fun getFromId(musicId: UUID): Flow<Music?> =
        musicDao.getFromId(musicId)

    override fun getAll(): Flow<List<Music>> = musicDao.getAll()

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDao.getAllMusicFromAlbum(albumId).first()
}