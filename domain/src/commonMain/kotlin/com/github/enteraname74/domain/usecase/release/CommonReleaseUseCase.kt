package com.github.enteraname74.domain.usecase.release

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.repository.ReleaseRepository
import kotlinx.coroutines.flow.Flow

class CommonReleaseUseCase(
    private val releaseRepository: ReleaseRepository,
) {
    suspend fun deleteLatest() {
        releaseRepository.deleteLatestRelease()
    }

    suspend fun fetchLatest() {
        releaseRepository.fetchLatestRelease()
    }

    fun getLatest(): Flow<Release?> =
        releaseRepository.getLatestRelease()

    fun getLatestViewedReleaseTag(): Flow<String?> =
        releaseRepository.getLatestViewedReleaseTag()
}