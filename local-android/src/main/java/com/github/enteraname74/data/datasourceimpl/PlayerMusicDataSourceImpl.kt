package com.github.enteraname74.data.datasourceimpl

import com.github.enteraname74.data.AppDatabase
import com.github.enteraname74.data.model.toPlayerWithMusicItem
import com.github.enteraname74.data.model.toRoomPlayerMusic
import com.github.enteraname74.domain.datasource.PlayerMusicDataSource
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

internal class PlayerMusicDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : PlayerMusicDataSource {
    override suspend fun insertPlayerMusic(playerMusic: PlayerMusic) {
        appDatabase.playerMusicDao.insertPlayerMusic(
            roomPlayerMusic = playerMusic.toRoomPlayerMusic()
        )
    }

    override suspend fun deleteMusicFromPlayerList(musicId: UUID) {
        appDatabase.playerMusicDao.deleteMusicFromPlayerList(
            musicId = musicId
        )
    }

    override suspend fun deleteAllPlayerMusic() {
        appDatabase.playerMusicDao.deleteAllPlayerMusic()
    }

    override suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) {
        appDatabase.playerMusicDao.insertAllPlayerMusics(
            playlist = playlist.map { it.toRoomPlayerMusic() }
        )
    }

    override suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem> {
        return appDatabase.playerMusicDao.getAllPlayerMusics().map { it.toPlayerWithMusicItem() }
    }

    override fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>> {
        return appDatabase.playerMusicDao.getAllPlayerMusicsAsFlow().map { list ->
            list.map { it.toPlayerWithMusicItem() }
        }
    }
}