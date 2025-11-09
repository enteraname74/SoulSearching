package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomPlayerMusic
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.soulsearching.repository.datasource.PlayerMusicDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class RoomPlayerMusicDataSourceImpl(
    private val appDatabase: AppDatabase
) : PlayerMusicDataSource {
    override suspend fun upsert(playerMusic: PlayerMusic) {
        appDatabase.playerMusicDao.upsert(
            roomPlayerMusic = playerMusic.toRoomPlayerMusic()
        )
    }

    override suspend fun delete(musicId: UUID) {
        appDatabase.playerMusicDao.delete(
            musicId = musicId
        )
    }

    override suspend fun deleteAll() {
        appDatabase.playerMusicDao.deleteAll()
    }

    override suspend fun upsertAll(playlist: List<PlayerMusic>) {
        appDatabase.playerMusicDao.upsertAll(
            playlist = playlist.map { it.toRoomPlayerMusic() }
        )
    }

    override fun getAll(): Flow<List<PlayerWithMusicItem>> {
        return appDatabase.playerMusicDao.getAll().map { list ->
            list.map { it.toPlayerWithMusicItem()  }
        }
    }
}