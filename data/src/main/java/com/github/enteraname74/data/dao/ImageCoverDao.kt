package com.github.enteraname74.data.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.RoomImageCover
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
internal interface ImageCoverDao {
    @Upsert
    suspend fun insertImageCover(roomImageCover: RoomImageCover)

    @Delete
    suspend fun deleteImageCover(roomImageCover: RoomImageCover)

    @Query("DELETE FROM RoomImageCover WHERE coverId = :coverId")
    suspend fun deleteFromCoverId(coverId: UUID)

    @Query("SELECT RoomImageCover.* FROM RoomImageCover WHERE coverId = :coverId LIMIT 1")
    suspend fun getCoverOfElement(coverId : UUID) : RoomImageCover?

    @Query("SELECT * FROM RoomImageCover")
    fun getAllCovers() : Flow<List<RoomImageCover>>
}