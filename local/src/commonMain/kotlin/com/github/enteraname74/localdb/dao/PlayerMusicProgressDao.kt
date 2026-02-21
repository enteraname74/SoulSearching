package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PlayerMusicProgressDao {
    @Upsert
    suspend fun upsert(progress: RoomPlayerMusicProgress)

    @Query(
        """
            SELECT * FROM RoomPlayerMusicProgress 
            WHERE playedListId = :listId
            LIMIT 1
        """
    )
    fun getCurrent(listId: UUID): Flow<RoomPlayerMusicProgress?>
}