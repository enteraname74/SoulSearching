package com.github.enteraname74.domain.usecase.cover

import com.github.enteraname74.domain.repository.CoverRepository
import java.util.UUID

class DeleteCoverUseCase(
    private val coverRepository: CoverRepository,
) {
    suspend operator fun invoke(coverId: UUID) {
        coverRepository.delete(
            coverId = coverId,
        )
    }
}