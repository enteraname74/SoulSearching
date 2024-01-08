package com.github.enteraname74.datasource
import com.github.enteraname74.model.ImageCover
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of an ImageCover.
 */
interface ImageCoverDataSource {
    /**
     * Inserts or updates an ImageCover
     */
    suspend fun insertImageCover(imageCover: ImageCover)

    /**
     * Deletes an ImageCover.
     */
    suspend fun deleteImageCover(imageCover: ImageCover)

    /**
     * Deletes an ImageCover from its id.
     */
    suspend fun deleteFromCoverId(coverId: UUID)

    /**
     * Get an ImageCover from its id.
     */
    suspend fun getCoverOfElement(coverId : UUID) : ImageCover?

    /**
     * Retrieves a flow of all ImageCover.
     */
    fun getAllCoversAsFlow() : Flow<List<ImageCover>>
}