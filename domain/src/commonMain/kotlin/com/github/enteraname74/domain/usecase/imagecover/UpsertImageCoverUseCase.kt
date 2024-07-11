package com.github.enteraname74.domain.usecase.imagecover

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository

class UpsertImageCoverUseCase(
    private val imageCoverRepository: ImageCoverRepository,
) {
    suspend operator fun invoke(imageCover: ImageCover) {
        imageCoverRepository.upsert(
            imageCover = imageCover,
        )
    }
}