package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.PlayerMusicDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of a PlayerMusic.
 */
class PlayerMusicRepositoryImpl(
    private val playerMusicDataSource: PlayerMusicDataSource
): PlayerMusicRepository {
    /**
     * Inserts or updates a PlayerMusic.
     */
    override suspend fun upsertMusicToPlayerList(playerMusic: PlayerMusic) =
        playerMusicDataSource.upsert(
            playerMusic = playerMusic
        )

    /**
     * Deletes a PlayerMusic from a given musicId.
     */
    override suspend fun deleteMusicFromPlayerList(musicId: UUID) =
        playerMusicDataSource.delete(
            musicId = musicId
        )

    /**
     * Deletes all PlayerMusic.
     */
    override suspend fun deleteAll() = playerMusicDataSource.deleteAll()

    /**
     * Inserts a list of PlayerMusic.
     */
    override suspend fun upsertAll(playlist: List<PlayerMusic>) =
        playerMusicDataSource.upsertAll(
            playlist = playlist
        )

    /**
     * Retrieves a flow of all PlayerWithMusicItem.
     */
    override fun getAll(): Flow<List<PlayerWithMusicItem>> =
        playerMusicDataSource.getAll()
}