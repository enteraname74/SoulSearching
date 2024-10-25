package com.github.enteraname74.soulsearching.features.filemanager.di

import org.koin.core.module.Module
import org.koin.dsl.module
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManagerAndroidImpl
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManagerAndroidImpl
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.MusicFetcherAndroidImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual val fileManagerPlatformModule: Module = module {
    singleOf(::CoverFileManagerAndroidImpl) bind CoverFileManager::class
    singleOf(::MusicFetcherAndroidImpl) bind MusicFetcher::class
    singleOf(::CachedCoverManagerAndroidImpl) bind CachedCoverManager::class
}