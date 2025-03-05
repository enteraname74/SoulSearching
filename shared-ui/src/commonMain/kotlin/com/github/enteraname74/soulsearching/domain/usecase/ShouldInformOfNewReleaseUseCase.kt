package com.github.enteraname74.soulsearching.domain.usecase

import com.github.enteraname74.domain.usecase.release.GetLatestReleaseUseCase
import com.github.enteraname74.domain.usecase.release.GetLatestViewedReleaseUseCase
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ShouldInformOfNewReleaseUseCase(
    private val getLatestReleaseUseCase: GetLatestReleaseUseCase,
    private val getLatestViewedReleaseUseCase: GetLatestViewedReleaseUseCase,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        getLatestReleaseUseCase(),
        getLatestViewedReleaseUseCase(),
    ) { latestRelease, latestViewedReleaseName ->
        latestRelease?.let { release ->
            release.isNewerThanCurrentVersion() && latestViewedReleaseName != release.tag
        } ?: false
    }
}