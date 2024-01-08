package com.github.enteraname74.datasource
import com.github.enteraname74.model.PlayerMusic
import com.github.enteraname74.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a PlayerMusic.
 */
interface PlayerMusicDataSource {
    /**
     * Inserts or updates a PlayerMusic.
     */
    suspend fun insertPlayerMusic(playerMusic: PlayerMusic)

    /**
     * Deletes a PlayerMusic from a given musicId.
     */
    suspend fun deleteMusicFromPlayerList(musicId: UUID)

    /**
     * Deletes all PlayerMusic.
     */
    suspend fun deleteAllPlayerMusic()

    /**
     * Inserts a list of PlayerMusic.
     */
    suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>)

    /**
     * Retrieves all PlayerWithMusicItem.
     */
    suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem>

    /**
     * Retrieves a flow of all PlayerWithMusicItem.
     */
    fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>>
}