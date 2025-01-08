package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicDao
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

internal class MusicLocalDataSourceImpl(
    private val musicDao: MusicDao
): MusicLocalDataSource {
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

    override suspend fun deleteAll(ids: List<UUID>) {
        musicDao.deleteAll(
            ids = ids,
        )
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        musicDao.deleteAll(
            dataMode = dataMode.value,
        )
    }

    override suspend fun getFromPath(musicPath: String): Music? =
        musicDao.getFromPath(musicPath)

    override fun getFromId(musicId: UUID): Flow<Music?> =
        musicDao.getFromId(musicId)

    override fun getAll(dataMode: DataMode): Flow<List<Music>> = musicDao.getAll(
        dataMode = dataMode.value
    )

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDao.getAllMusicFromAlbum(albumId).first()
}