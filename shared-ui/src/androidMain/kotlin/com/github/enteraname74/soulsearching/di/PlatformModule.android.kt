package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.enteraname74.soulsearching.model.utils.MusicFetcherAndroidImpl
import com.github.enteraname74.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::AllMusicsViewModelAndroidImpl) bind AllMusicsViewModel::class
    singleOf(::MusicFetcherAndroidImpl) bind MusicFetcher::class
    singleOf(::PlaybackManagerAndroidImpl) bind PlaybackManager::class
}