package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.PlaylistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.SortingInformationDelegateImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val delegateModule = module {
    factoryOf(::MusicBottomSheetDelegateImpl)
    factoryOf(::PlaylistBottomSheetDelegateImpl)
    factoryOf(::AlbumBottomSheetDelegateImpl)
    factoryOf(::ArtistBottomSheetDelegateImpl)
    singleOf(::SortingInformationDelegateImpl)
}