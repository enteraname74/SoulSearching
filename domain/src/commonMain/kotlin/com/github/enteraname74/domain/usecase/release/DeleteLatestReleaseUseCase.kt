package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.repository.ReleaseRepository

class DeleteLatestReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    suspend operator fun invoke() {
        releaseRepository.deleteLatestRelease()
    }
}