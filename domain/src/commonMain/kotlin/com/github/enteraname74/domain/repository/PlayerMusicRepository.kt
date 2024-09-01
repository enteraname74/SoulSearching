package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PlayerMusicRepository {
    /**
     * Inserts or updates a PlayerMusic.
     */
    suspend fun upsertMusicToPlayerList(playerMusic: PlayerMusic)

    /**
     * Deletes a PlayerMusic from a given musicId.
     */
    suspend fun deleteMusicFromPlayerList(musicId: UUID)

    /**
     * Deletes all PlayerMusic.
     */
    suspend fun deleteAll()

    /**
     * Upsert a list of PlayerMusic.
     */
    suspend fun upsertAll(playlist: List<PlayerMusic>)

    /**
     * Retrieves a flow of all PlayerWithMusicItem.
     */
    fun getAll(): Flow<List<PlayerWithMusicItem>>
}