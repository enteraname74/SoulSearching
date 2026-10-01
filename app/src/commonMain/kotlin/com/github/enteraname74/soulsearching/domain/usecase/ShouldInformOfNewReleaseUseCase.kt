package com.github.enteraname74.soulsearching.domain.usecase

import com.github.enteraname74.soulsearching.domain.model.SoulPlatform
import com.github.enteraname74.soulsearching.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class ShouldInformOfNewReleaseUseCase(
    private val commonReleaseUseCase: CommonReleaseUseCase,
) {
    /*
    New release is only shown for desktop and android platforms.
     */
    operator fun invoke(): Flow<Boolean> = if (SoulPlatformUtils.platform == SoulPlatform.Web) {
        flowOf(false)
    } else {
        combine(
            commonReleaseUseCase.getLatest(),
            commonReleaseUseCase.getLatestViewedReleaseTag(),
        ) { latestRelease, latestViewedReleaseName ->
            latestRelease?.let { release ->
                release.isNewerThanCurrentVersion() && latestViewedReleaseName != release.tag
            } ?: false
        }
    }
}
