package com.github.enteraname74.soulsearching.features.musicmanager.di

import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcherWebImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val musicManagerModule: Module = module {
    singleOf(::MusicFetcherWebImpl) bind MusicFetcher::class
}