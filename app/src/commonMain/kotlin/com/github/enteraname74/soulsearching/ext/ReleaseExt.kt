package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.soulsearching.domain.AppVersion

fun Release.isNewerThanCurrentVersion(): Boolean {
    val currentVersion = AppVersion.versionName.split("-").firstOrNull() ?: return false
    val cleanedReleaseVersion = this.tag.replace("v", "")

    return currentVersion != cleanedReleaseVersion
}