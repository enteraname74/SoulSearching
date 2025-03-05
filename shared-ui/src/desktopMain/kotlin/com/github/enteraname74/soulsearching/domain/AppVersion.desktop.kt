package com.github.enteraname74.soulsearching.domain

import com.github.enteraname74.domain.util.AppEnvironment

actual object AppVersion {
    private val suffix: String = if (AppEnvironment.IS_IN_DEVELOPMENT) {
        "-dev"
    } else {
        ""
    }

    actual val versionName = "0.12.0$suffix"
}