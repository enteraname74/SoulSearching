package com.github.enteraname74.domain.repository
import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.model.ImageCover
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of an ImageCover.
 */
class ImageCoverRepository(
    private val imageCoverDataSource: ImageCoverDataSource
) {
    /**
     * Inserts or updates an ImageCover
     */
    suspend fun insertImageCover(imageCover: ImageCover) = imageCoverDataSource.insertImageCover(
        imageCover = imageCover
    )

    /**
     * Deletes an ImageCover.
     */
    suspend fun deleteImageCover(imageCover: ImageCover) = imageCoverDataSource.deleteImageCover(
        imageCover = imageCover
    )

    /**
     * Deletes an ImageCover from its id.
     */
    suspend fun deleteFromCoverId(coverId: UUID) = imageCoverDataSource.deleteFromCoverId(
        coverId = coverId
    )

    /**
     * Get an ImageCover from its id.
     */
    suspend fun getCoverOfElement(coverId: UUID): ImageCover? =
        imageCoverDataSource.getCoverOfElement(
            coverId = coverId
        )

    /**
     * Retrieves a flow of all ImageCover.
     */
    fun getAllCoversAsFlow(): Flow<List<ImageCover>> = imageCoverDataSource.getAllCoversAsFlow()
}