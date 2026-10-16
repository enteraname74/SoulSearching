package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SoulResult

actual class LatestReleaseDownloader {
    actual suspend fun downloadLatestRelease(release: Release): SoulResult<Unit> =
        SoulResult.Error("Release downloads are only supported on Android")
}
