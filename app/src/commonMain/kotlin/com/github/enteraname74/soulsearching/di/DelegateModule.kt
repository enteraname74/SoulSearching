package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiAlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.SortingInformationDelegateImpl
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.ArtistCoverFolderRetrieverViewModelDelegate
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val delegateModule = module {
    factoryOf(::AlbumBottomSheetDelegateImpl)
    factoryOf(::ArtistBottomSheetDelegateImpl)

    factoryOf(::MultiAlbumBottomSheetDelegateImpl)
    factoryOf(::MultiArtistBottomSheetDelegateImpl)

    singleOf(::SortingInformationDelegateImpl)

    singleOf(::ArtistCoverFolderRetrieverViewModelDelegate)
}