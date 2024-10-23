package com.github.enteraname74.soulsearching.features.filemanager.di

import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import com.github.enteraname74.soulsearching.features.filemanager.usecase.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val fileManagerModule: Module = module {
    includes(fileManagerPlatformModule)
    singleOf(::MusicFileUpdater)

    // Use cases
    singleOf(::UpdateMusicUseCase)
    singleOf(::UpdateAlbumUseCase)
    singleOf(::UpdateArtistUseCase)
}