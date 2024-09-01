package com.github.enteraname74.domain.usecase.imagecover

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import kotlinx.coroutines.flow.Flow

class GetAllImageCoverUseCase(
    private val imageCoverRepository: ImageCoverRepository,
) {
    operator fun invoke(): Flow<List<ImageCover>> =
        imageCoverRepository.getAll()
}