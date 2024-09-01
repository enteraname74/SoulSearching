package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.ImageCover
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ImageCoverRepository {
    /**
     * Inserts or updates an ImageCover
     */
    suspend fun upsert(imageCover: ImageCover)

    /**
     * Deletes an ImageCover.
     */
    suspend fun delete(imageCover: ImageCover)

    /**
     * Deletes an ImageCover from its id.
     */
    suspend fun delete(coverId: UUID)

    /**
     * Get an ImageCover from its id.
     */
    suspend fun getCoverOfElement(coverId: UUID): ImageCover?
    /**
     * Retrieves a flow of all ImageCover.
     */
    fun getAll(): Flow<List<ImageCover>>
}