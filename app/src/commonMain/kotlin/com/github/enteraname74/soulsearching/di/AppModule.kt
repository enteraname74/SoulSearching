package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepManager
import com.github.enteraname74.soulsearching.feature.tabmanager.TabManager
import com.github.enteraname74.soulsearching.shareddi.mainModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule: Module = module {

    includes(
        mainModule,
        platformModule,
        viewModelModule,
        delegateModule,
    )
    singleOf(::PlayerViewManager)
    singleOf(::LoadingManager)
    singleOf(::TabManager)
    singleOf(::PlayerMusicListViewManager)
    singleOf(::AddNewsSongsStepManager)

    singleOf(::MultiSelectionManager)

    singleOf(::ShouldInformOfNewReleaseUseCase)
}