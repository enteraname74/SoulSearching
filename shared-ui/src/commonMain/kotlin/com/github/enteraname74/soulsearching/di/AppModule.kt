package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.shareddi.mainModule
import org.koin.core.module.Module
import org.koin.dsl.module
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import org.koin.core.module.dsl.singleOf

val appModule: Module = module {
    includes(
        mainModule,
        platformModule,
        viewModelModule,
        delegateModule,
    )
    singleOf(::PlayerViewManager)
    singleOf(::PlayerMusicListViewManager)
    singleOf(::ImageCoverRetriever)
}