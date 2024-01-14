package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.PlayerMusicDataSource
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ExposedPlayerMusicDataSourceImpl: PlayerMusicDataSource {
    override suspend fun insertPlayerMusic(playerMusic: PlayerMusic) {

    }

    override suspend fun deleteMusicFromPlayerList(musicId: UUID) {

    }

    override suspend fun deleteAllPlayerMusic() {

    }

    override suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) {

    }

    override suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem> {
        return emptyList()
    }

    override fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>> {
        return flowOf(emptyList())
    }
}