package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.ReleaseRepository
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReleaseRepositoryImpl(
    private val releaseDataSource: ReleaseDataSource,
    private val settings: SoulSearchingSettings,
): ReleaseRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatestRelease(): Flow<Release?> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Release.LATEST_RELEASE).mapLatest { json ->
            runCatching {
                Json.decodeFromString<Release>(json)
            }.getOrNull()
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatestViewedReleaseTag(): Flow<String?> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Release.LATEST_VIEWED_RELEASE).mapLatest {
            it.takeIf { it.isNotBlank() }
        }

    override suspend fun fetchLatestRelease() {
        val latestRelease: Release? = releaseDataSource.getLatestRelease()
        settings.set(
            key = SoulSearchingSettingsKeys.Release.LATEST_RELEASE.key,
            value = Json.encodeToString(latestRelease)
        )
    }

    override suspend fun setLatestViewedReleaseTag(name: String) {
        settings.set(
            key = SoulSearchingSettingsKeys.Release.LATEST_VIEWED_RELEASE.key,
            value = name,
        )
    }
}