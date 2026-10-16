package com.github.enteraname74.soulsearching.domain.usecase.release

import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.repository.ReleaseRepository
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

    suspend fun markViewed(tag: String) {
        releaseRepository.markReleaseViewed(tag)
    }

    fun getLatest(): Flow<Release?> =
        releaseRepository.getLatestRelease()

    fun getLatestViewedReleaseTag(): Flow<String?> =
        releaseRepository.getLatestViewedReleaseTag()
}
