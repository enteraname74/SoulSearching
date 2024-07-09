package com.github.enteraname74.soulsearching.feature.coversprovider

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.usecase.imagecover.DeleteImageCoverUseCase
import com.github.enteraname74.domain.usecase.imagecover.IsImageCoverUsedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.*

/**
 * Handler for managing the AllImageCoversViewModel.
 */
class AllImageCoversViewModel(
    imageCoverRepository: ImageCoverRepository,
    private val isImageCoverUsedUseCase: IsImageCoverUsedUseCase,
    private val deleteImageCoverUseCase: DeleteImageCoverUseCase,
): ScreenModel {
    private val _covers = imageCoverRepository.getAll()
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(ImageCoverState())
    val state = combine(
        _state,
        _covers
    ) { state, covers ->
        return@combine state.copy(
            covers = covers as ArrayList<ImageCover>
        )

    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImageCoverState()
    )

    /**
     * Tries to retrieve an ImageBitmap representation of the id of an image cover.
     */
    fun getImageCover(coverId: UUID?): ImageBitmap? {
        return state.value.covers.find { it.coverId == coverId }?.cover
    }

    /**
     * Delete an image if it's not used by a song, an album, an artist or a playlist.
     */
    suspend fun deleteImageIfNotUsed(coverId: UUID) {
        if (isImageCoverUsedUseCase(coverId = coverId)) {
            deleteImageCoverUseCase(coverId = coverId)
        }
    }
}