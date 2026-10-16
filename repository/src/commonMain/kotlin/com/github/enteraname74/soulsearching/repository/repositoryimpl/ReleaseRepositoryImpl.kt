package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.repository.ReleaseRepository
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class ReleaseRepositoryImpl(
    private val releaseDataSource: ReleaseDataSource,
    private val settingsRepository: SettingsRepository,
) : ReleaseRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatestRelease(): Flow<Release?> =
        settingsRepository.observeRelease().mapLatest { settings ->
            settings.latestRelease.takeIf { settings.isFetchReleaseFromGithubEnabled }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatestViewedReleaseTag(): Flow<String?> =
        settingsRepository.observeRelease().mapLatest { it.latestViewedReleaseTag }

    override suspend fun fetchLatestRelease() {
        if (!settingsRepository.getSettings().release.isFetchReleaseFromGithubEnabled) return

        val latestRelease: Release? = releaseDataSource.getLatestRelease()
        if (settingsRepository.getSettings().release.isFetchReleaseFromGithubEnabled) {
            settingsRepository.setLatestRelease(latestRelease)
        }
    }

    override suspend fun markReleaseViewed(tag: String) {
        settingsRepository.markLatestReleaseViewed(tag)
    }

    override suspend fun deleteLatestRelease() {
        settingsRepository.clearLatestRelease()
    }
}
