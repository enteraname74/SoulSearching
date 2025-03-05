package com.github.enteraname74.soulsearching.features.playback.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

val playbackModule: Module = module {
    includes(playbackPlatformModule)
    singleOf(::PlaybackManager)
}