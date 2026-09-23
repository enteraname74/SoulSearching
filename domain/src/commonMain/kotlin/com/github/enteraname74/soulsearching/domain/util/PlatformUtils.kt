package com.github.enteraname74.soulsearching.domain.util

import com.github.enteraname74.soulsearching.domain.model.Platform

expect object PlatformUtils {
    val platform: Platform
}

val PlatformUtils.isNonAndroid: Boolean
    get() = platform != Platform.Android