package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.repository.ReleaseRepository
import kotlinx.coroutines.flow.Flow

class GetLatestReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    operator fun invoke(): Flow<Release?> = releaseRepository.getLatestRelease()
}