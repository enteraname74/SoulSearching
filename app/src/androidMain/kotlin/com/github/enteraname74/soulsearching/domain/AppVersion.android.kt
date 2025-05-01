package com.github.enteraname74.soulsearching.domain
import com.github.soulsearching.BuildConfig


actual object AppVersion {
    actual val versionName: String
        get() = BuildConfig.VERSION_NAME
}