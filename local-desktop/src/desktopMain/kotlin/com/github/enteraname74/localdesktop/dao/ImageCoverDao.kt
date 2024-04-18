package com.github.enteraname74.localdesktop.dao

import com.github.enteraname74.domain.model.ImageCover
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO for managing ImageCovers.
 */
internal interface ImageCoverDao {
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