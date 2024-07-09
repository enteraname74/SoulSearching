package com.github.enteraname74.domain.usecase.imagecover

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import java.util.UUID

class GetCoverOfElementUseCase(
    private val imageCoverRepository: ImageCoverRepository,
) {
    suspend operator fun invoke(coverId: UUID): ImageCover? =
        imageCoverRepository.getCoverOfElement(
            coverId = coverId,
        )
}