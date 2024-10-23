package com.github.enteraname74.domain.usecase.cover

import com.github.enteraname74.domain.repository.CoverRepository
import java.util.UUID

class UpsertImageCoverUseCase(
    private val coverRepository: CoverRepository,
) {
    suspend operator fun invoke(
        id: UUID,
        data: ByteArray,
    ) {
        coverRepository.upsert(
            id = id,
            data = data,
        )
    }
}