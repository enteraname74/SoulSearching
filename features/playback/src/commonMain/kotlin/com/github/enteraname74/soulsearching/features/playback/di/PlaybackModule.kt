package com.github.enteraname74.soulsearching.features.playback.di

import org.koin.core.module.Module
import org.koin.dsl.module

val playbackModule: Module = module {
    includes(playbackPlatformModule)
}