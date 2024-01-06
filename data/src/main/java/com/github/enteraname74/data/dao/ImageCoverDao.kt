package com.github.enteraname74.data.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.ImageCover
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
internal interface ImageCoverDao {
    @Upsert
    suspend fun insertImageCover(imageCover: ImageCover)

    @Delete
    suspend fun deleteImageCover(imageCover: ImageCover)

    @Query("DELETE FROM ImageCover WHERE coverId = :coverId")
    suspend fun deleteFromCoverId(coverId: UUID)

    @Query("SELECT ImageCover.* FROM ImageCover WHERE coverId = :coverId LIMIT 1")
    suspend fun getCoverOfElement(coverId : UUID) : ImageCover?

    @Query("SELECT * FROM ImageCover")
    fun getAllCovers() : Flow<List<ImageCover>>
}