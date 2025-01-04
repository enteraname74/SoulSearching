package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.repository.ReleaseRepository

class GetLatestReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    suspend operator fun invoke(): Release? = releaseRepository.getLatestRelease()
}