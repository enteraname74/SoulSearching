package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.repository.ReleaseRepository

class SetLatestViewedReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    suspend operator fun invoke(releaseTag: String) {
        releaseRepository.setLatestViewedReleaseTag(releaseTag)
    }
}