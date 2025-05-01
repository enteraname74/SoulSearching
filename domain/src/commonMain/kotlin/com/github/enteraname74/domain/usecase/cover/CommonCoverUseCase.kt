package com.github.enteraname74.domain.usecase.cover

import com.github.enteraname74.domain.repository.CoverRepository
import java.util.*

class CommonCoverUseCase(
    private val coverRepository: CoverRepository,
) {
    suspend fun upsert(
        id: UUID,
        data: ByteArray,
    ) {
        coverRepository.upsert(
            id = id,
            data = data,
        )
    }

    suspend fun delete(coverId: UUID) {
        coverRepository.delete(
            coverId = coverId,
        )
    }

    suspend fun getAllCoverIds(): List<UUID> =
        coverRepository.getAllCoverIds()
}