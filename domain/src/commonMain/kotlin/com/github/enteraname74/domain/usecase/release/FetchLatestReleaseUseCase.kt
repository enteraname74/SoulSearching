package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.repository.ReleaseRepository

class FetchLatestReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    suspend operator fun invoke() {
        releaseRepository.fetchLatestRelease()
    }
}