package com.github.enteraname74.soulsearching.feature.coversprovider

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.usecase.imagecover.DeleteImageCoverUseCase
import com.github.enteraname74.domain.usecase.imagecover.IsImageCoverUsedUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Class used for retrieving [ImageCover] of elements.
 */
class ImageCoverRetriever(
    private val imageCoverRepository: ImageCoverRepository,
    private val isImageCoverUsedUseCase: IsImageCoverUsedUseCase,
    private val deleteImageCoverUseCase: DeleteImageCoverUseCase,
) {
    val allCovers: StateFlow<List<ImageCover>> = imageCoverRepository.getAll()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    /**
     * Tries to retrieve an ImageBitmap representation of the id of an image cover.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getImageBitmap(coverId: UUID?): Flow<ImageBitmap?> {
        return allCovers.mapLatest { imageCovers ->
            imageCovers.find { it.coverId == coverId }?.cover
        }
    }

    /**
     * Delete an image if it's not used by a song, an album, an artist or a playlist.
     */
    suspend fun deleteImageIfNotUsed(coverId: UUID) {
        if (!isImageCoverUsedUseCase(coverId = coverId)) {
            deleteImageCoverUseCase(coverId = coverId)
        }
    }
}