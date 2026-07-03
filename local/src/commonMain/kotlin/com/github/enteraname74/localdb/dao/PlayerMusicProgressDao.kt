package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PlayerMusicProgressDao {
    @Upsert
    suspend fun upsert(progress: RoomPlayerMusicProgress)

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
