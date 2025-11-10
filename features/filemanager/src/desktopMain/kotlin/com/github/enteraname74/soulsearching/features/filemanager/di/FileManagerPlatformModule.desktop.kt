package com.github.enteraname74.soulsearching.features.filemanager.di

import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManagerDesktopImpl
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManagerDesktopImpl
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val fileManagerPlatformModule: Module = module {
    singleOf(::CoverFileManagerDesktopImpl) bind CoverFileManager::class
    singleOf(::CachedCoverManagerDesktopImpl) bind CachedCoverManager::class
    singleOf(::MusicMetadataHelper)
}