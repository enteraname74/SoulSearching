package com.github.enteraname74.soulsearching.repository.datasource
import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a PlayerMusic.
 */
interface PlayerMusicDataSource {
    /**
     * Inserts or updates a PlayerMusic.
     */
    suspend fun upsert(playerMusic: PlayerMusic)

    /**
     * Deletes a PlayerMusic from a given musicId.
     */
    suspend fun delete(musicId: UUID)

    /**
     * Deletes all PlayerMusic.
     */
    suspend fun deleteAll()

    /**
     * Inserts a list of PlayerMusic.
     */
    suspend fun upsertAll(playlist: List<PlayerMusic>)

    /**
     * Retrieves a flow of all PlayerWithMusicItem.
     */
    fun getAll(): Flow<List<PlayerWithMusicItem>>
}