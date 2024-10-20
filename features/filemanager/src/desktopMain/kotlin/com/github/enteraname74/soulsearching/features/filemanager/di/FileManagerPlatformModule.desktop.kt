package com.github.enteraname74.soulsearching.features.filemanager.di

import org.koin.core.module.Module
import org.koin.dsl.module
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManagerDesktopImpl
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.MusicFetcherDesktopImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual val fileManagerPlatformModule: Module = module {
    singleOf(::CoverFileManagerDesktopImpl) bind CoverFileManager::class
    singleOf(::MusicFetcherDesktopImpl) bind MusicFetcher::class
}