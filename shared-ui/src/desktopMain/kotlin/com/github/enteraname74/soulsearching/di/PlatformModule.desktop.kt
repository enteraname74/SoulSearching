package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.model.MusicFetcherDesktopImpl
import com.github.enteraname74.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.enteraname74.soulsearching.viewmodel.AllMusicsViewModelDesktopImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::AllMusicsViewModelDesktopImpl) bind AllMusicsViewModel::class
    singleOf(::MusicFetcherDesktopImpl) bind MusicFetcher::class
    singleOf(::PlaybackManagerDesktopImpl) bind PlaybackManager::class
}