package com.github.enteraname74.soulsearching.domain.util

import com.github.enteraname74.soulsearching.domain.model.SoulPlatform

expect object SoulPlatformUtils {
    val platform: SoulPlatform
}

val SoulPlatformUtils.isNonAndroid: Boolean
    get() = platform != SoulPlatform.Android

val SoulPlatformUtils.isWeb: Boolean
    get() = platform == SoulPlatform.Web