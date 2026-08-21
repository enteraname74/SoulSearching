package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PlayerMusicProgressDao {
    @Query(
        """
            INSERT INTO RoomPlayerMusicProgress (
                id, playedListId, playerMusicId, progress
            )
            VALUES (:id, :playedListId, :playerMusicId, :progress)
            ON CONFLICT(id) DO UPDATE SET
                playedListId = excluded.playedListId,
                playerMusicId = excluded.playerMusicId,
                progress = excluded.progress
        """
    )
    suspend fun upsert(
        id: String,
        playedListId: Uuid,
        playerMusicId: String,
        progress: Int,
    )

    @Query(
        """
            SELECT * FROM RoomPlayerMusicProgress 
            WHERE playedListId = :listId AND playerMusicId = :playerMusicId
            LIMIT 1
        """
    )
    fun getCurrent(
        listId: Uuid,
        playerMusicId: String,
    ): Flow<RoomPlayerMusicProgress?>
}
