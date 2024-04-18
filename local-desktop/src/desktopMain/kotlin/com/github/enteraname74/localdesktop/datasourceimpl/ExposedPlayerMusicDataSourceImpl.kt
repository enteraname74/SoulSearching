package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.PlayerMusicDataSource
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.localdesktop.dao.PlayerMusicDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the PlayerMusicDataSource with Exposed.
 */
internal class ExposedPlayerMusicDataSourceImpl(
    private val playerMusicDao: PlayerMusicDao
) : PlayerMusicDataSource {
    override suspend fun insertPlayerMusic(playerMusic: PlayerMusic) =
        playerMusicDao.insertPlayerMusic(playerMusic = playerMusic)

    override suspend fun deleteMusicFromPlayerList(musicId: UUID) =
        playerMusicDao.deleteMusicFromPlayerList(musicId = musicId)

    override suspend fun deleteAllPlayerMusic() =
        playerMusicDao.deleteAllPlayerMusic()

    override suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) =
        playerMusicDao.insertAllPlayerMusics(playlist = playlist)

    override suspend fun getAllPlayerMusics() =
        playerMusicDao.getAllPlayerMusics()

    override fun getAllPlayerMusicsAsFlow() = playerMusicDao.getAllPlayerMusicsAsFlow()
}