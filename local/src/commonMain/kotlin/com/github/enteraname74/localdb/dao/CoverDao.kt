package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import kotlin.uuid.Uuid

@Dao
interface CoverDao {
    @Query(
        """
            SELECT 
                EXISTS (SELECT 1 FROM RoomMusic WHERE coverId = :coverId)
                OR EXISTS (SELECT 1 FROM RoomAlbum WHERE coverId = :coverId)
                OR EXISTS (SELECT 1 FROM RoomPlaylist WHERE coverId = :coverId)
                OR EXISTS (SELECT 1 FROM RoomArtist WHERE coverId = :coverId)
        """
    )
    suspend fun isCoverUsed(coverId: Uuid): Boolean
}
