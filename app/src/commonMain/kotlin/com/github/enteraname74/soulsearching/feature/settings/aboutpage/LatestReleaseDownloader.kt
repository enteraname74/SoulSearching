package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SoulResult

expect class LatestReleaseDownloader {
    suspend fun downloadLatestRelease(release: Release): SoulResult<Unit>
}
