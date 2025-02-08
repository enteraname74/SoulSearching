package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.repository.ReleaseRepository
import kotlinx.coroutines.flow.Flow

class GetLatestViewedReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    operator fun invoke(): Flow<String?> =
        releaseRepository.getLatestViewedReleaseTag()
}