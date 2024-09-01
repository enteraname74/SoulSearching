package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.soulsearching.localdesktop.dao.PlayerMusicDao
import com.github.enteraname74.soulsearching.repository.datasource.PlayerMusicDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

internal class PlayerMusicDataSourceImpl(
    private val playerMusicDao: PlayerMusicDao
) : PlayerMusicDataSource {
    override suspend fun upsert(playerMusic: PlayerMusic) {
        playerMusicDao.upsert(playerMusic)
    }

    override suspend fun delete(musicId: UUID) {
        playerMusicDao.delete(musicId)
    }

    override suspend fun deleteAll() {
        playerMusicDao.deleteAll()
    }

    override suspend fun upsertAll(playlist: List<PlayerMusic>) {
        playerMusicDao.upsertAll(playlist)
    }

    override fun getAll(): Flow<List<PlayerWithMusicItem>> =
        playerMusicDao.getAll()
}