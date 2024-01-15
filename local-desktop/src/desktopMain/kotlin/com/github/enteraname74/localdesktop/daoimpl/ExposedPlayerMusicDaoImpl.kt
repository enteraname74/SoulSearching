package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.localdesktop.dao.PlayerMusicDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the PlayerMusicDao for Exposed.
 */
class ExposedPlayerMusicDaoImpl: PlayerMusicDao {
    override suspend fun insertPlayerMusic(playerMusic: PlayerMusic) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromPlayerList(musicId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPlayerMusic() {
        TODO("Not yet implemented")
    }

    override suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem> {
        TODO("Not yet implemented")
    }

    override fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>> {
        TODO("Not yet implemented")
    }
}