package com.github.enteraname74.domain.usecase.cover

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.CoverRepository
import java.util.UUID

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

    suspend fun deleteUnusedFileCovers() {
        val allCoverIds: List<UUID> = coverRepository.getAllCoverIds()
        allCoverIds.forEach { coverId ->
            if (!coverRepository.isCoverUsed(coverId = coverId)) {
                coverRepository.delete(coverId = coverId)
            }
        }
    }

    suspend fun getCoverImageBitmap(cover: Cover): ImageBitmap? =
        coverRepository.getCoverImageBitmap(cover)

    suspend fun getAllUniqueCover(covers: List<Cover>): List<ByteArray> =
        coverRepository.getAllUniqueCover(covers)
}