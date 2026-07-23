package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.playback.player.SignedPlaybackUrlProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual val playbackPlatformModule: Module = module {
    includes(nonAndroidPlatformModule)
    singleOf(::SignedPlaybackUrlProvider)
}