package com.github.enteraname74.repository

import com.github.enteraname74.datasource.PlayerMusicDataSource
import com.github.enteraname74.model.PlayerMusic
import com.github.enteraname74.model.PlayerWithMusicItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of a PlayerMusic.
 */
class PlayerMusicRepository(
    private val playerMusicDataSource: PlayerMusicDataSource
) {
    /**
     * Inserts or updates a PlayerMusic.
     */
    suspend fun insertPlayerMusic(playerMusic: PlayerMusic) =
        playerMusicDataSource.insertPlayerMusic(
            playerMusic = playerMusic
        )

    /**
     * Deletes a PlayerMusic from a given musicId.
     */
    suspend fun deleteMusicFromPlayerList(musicId: UUID) =
        playerMusicDataSource.deleteMusicFromPlayerList(
            musicId = musicId
        )

    /**
     * Deletes all PlayerMusic.
     */
    suspend fun deleteAllPlayerMusic() = playerMusicDataSource.deleteAllPlayerMusic()

    /**
     * Inserts a list of PlayerMusic.
     */
    suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>) =
        playerMusicDataSource.insertAllPlayerMusics(
            playlist = playlist
        )

    /**
     * Retrieves all PlayerWithMusicItem.
     */
    suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem> =
        playerMusicDataSource.getAllPlayerMusics()

    /**
     * Retrieves a flow of all PlayerWithMusicItem.
     */
    fun getAllPlayerMusicsAsFlow(): Flow<List<PlayerWithMusicItem>> =
        playerMusicDataSource.getAllPlayerMusicsAsFlow()
}