package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.SortingInformationDelegateImpl
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.ArtistCoverFolderRetrieverViewModelDelegate
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val delegateModule = module {
    singleOf(::SortingInformationDelegateImpl)
    singleOf(::ArtistCoverFolderRetrieverViewModelDelegate)
}