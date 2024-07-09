package com.github.enteraname74.domain.usecase.imagecover

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import java.util.UUID

class DeleteImageCoverUseCase(
    private val imageCoverRepository: ImageCoverRepository,
) {
    suspend operator fun invoke(imageCover: ImageCover) {
        imageCoverRepository.delete(
            imageCover = imageCover
        )
    }

    suspend operator fun invoke(coverId: UUID) {
        imageCoverRepository.delete(
            coverId = coverId,
        )
    }
}