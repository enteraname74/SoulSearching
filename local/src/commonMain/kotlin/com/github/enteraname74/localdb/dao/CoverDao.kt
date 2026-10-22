package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import kotlin.uuid.Uuid

@Dao
interface CoverDao {
    @Query(
        """
            SELECT 
                EXISTS (SELECT 1 FROM RoomMusic WHERE cover_fileCoverId = :coverId)
                OR EXISTS (SELECT 1 FROM RoomAlbum WHERE cover_fileCoverId = :coverId)
                OR EXISTS (SELECT 1 FROM RoomArtist WHERE cover_fileCoverId = :coverId)
                OR EXISTS (
                    SELECT 1 FROM RoomPlaylist WHERE
                        cover_simple_fileCoverId = :coverId
                        OR cover_grid_topStart_fileCoverId = :coverId
                        OR cover_grid_topEnd_fileCoverId = :coverId
                        OR cover_grid_bottomStart_fileCoverId = :coverId
                        OR cover_grid_bottomEnd_fileCoverId = :coverId
                )
                OR EXISTS (
                    SELECT 1 FROM RoomCollection WHERE
                        cover_simple_fileCoverId = :coverId
                        OR cover_grid_topStart_fileCoverId = :coverId
                        OR cover_grid_topEnd_fileCoverId = :coverId
                        OR cover_grid_bottomStart_fileCoverId = :coverId
                        OR cover_grid_bottomEnd_fileCoverId = :coverId
                )
        """
    )
    suspend fun isCoverUsed(coverId: Uuid): Boolean
}
