package com.github.enteraname74.soulsearching.feature.coversprovider

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.usecase.imagecover.DeleteImageCoverUseCase
import com.github.enteraname74.domain.usecase.imagecover.IsImageCoverUsedUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class used for retrieving [ImageCover] of elements.
 */
class ImageCoverRetriever(
    imageCoverRepository: ImageCoverRepository,
    private val isImageCoverUsedUseCase: IsImageCoverUsedUseCase,
    private val deleteImageCoverUseCase: DeleteImageCoverUseCase,
) {
    val allCovers: StateFlow<List<ImageCover>> = imageCoverRepository.getAll()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private val cachedCovers: ArrayList<ImageCover> = arrayListOf()
    private var deleteJob: Job? = null
    private var cleanImageLaunched: Boolean = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            allCovers.collect { covers ->
                if (covers.isNotEmpty() && !cleanImageLaunched) {
                    deleteJob = CoroutineScope(Dispatchers.IO).launch {
                        covers.forEach { cover ->
                            deleteImageIfNotUsed(coverId = cover.coverId)
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieve the latest image bitmap linked to a cover id.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getImageBitmap(coverId: UUID?): Flow<ImageBitmap?> {
        return allCovers.mapLatest { imageCovers ->
            val foundCover: ImageCover? = imageCovers.find { it.coverId == coverId }
            foundCover?.let {
                cachedCovers.removeIf { it.coverId == foundCover.coverId }
                cachedCovers.add(foundCover)
            }
            foundCover?.cover
        }
    }

    /**
     * Retrieve a default image bitmap, from the cached images, of the cover id.
     * To use when loading the latest image bitmap
     */
    fun getDefaultImageBitmap(coverId: UUID?): ImageBitmap? =
        cachedCovers.find { it.coverId == coverId }?.cover

    /**
     * Delete an image if it's not used by a song, an album, an artist or a playlist.
     */
    suspend fun deleteImageIfNotUsed(coverId: UUID) {
        if (!isImageCoverUsedUseCase(coverId = coverId)) {
            deleteImageCoverUseCase(coverId = coverId)
        }
    }
}