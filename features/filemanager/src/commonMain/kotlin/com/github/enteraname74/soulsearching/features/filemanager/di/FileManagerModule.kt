package com.github.enteraname74.soulsearching.features.filemanager.di

import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.filemanager.usecase.*
import com.github.enteraname74.soulsearching.features.httpclient.HttpClientNames
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val fileManagerModule: Module = module {
    includes(fileManagerPlatformModule)
    singleOf(::MusicFileUpdater)
    single {
        CoverRetriever(
            cachedCoverManager = get(),
            coverFileManager = get(),
            settings = get(),
            httpClient = get(named(HttpClientNames.CLOUD_AUTH)),
        )
    }

    // Use cases
    singleOf(::UpdateMusicUseCase)
    singleOf(::UpdateAlbumUseCase)
    singleOf(::UpdateArtistUseCase)
    singleOf(::UpdateArtistNameOfMusicUseCase)
}