package com.github.enteraname74.soulsearching.domain.usecase

import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ShouldInformOfNewReleaseUseCase(
    private val commonReleaseUseCase: CommonReleaseUseCase,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        commonReleaseUseCase.getLatest(),
        commonReleaseUseCase.getLatestViewedReleaseTag(),
    ) { latestRelease, latestViewedReleaseName ->
        latestRelease?.let { release ->
            release.isNewerThanCurrentVersion() && latestViewedReleaseName != release.tag
        } ?: false
    }
}