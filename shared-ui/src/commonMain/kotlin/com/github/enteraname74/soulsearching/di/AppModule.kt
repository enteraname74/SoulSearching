package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.shareddi.mainModule
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.MultipleArtistListener
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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
    singleOf(::PlayerMusicListViewManager)
    singleOf(::MultipleArtistListener)

    factoryOf(::MultiSelectionManagerImpl) bind MultiSelectionManager::class
}