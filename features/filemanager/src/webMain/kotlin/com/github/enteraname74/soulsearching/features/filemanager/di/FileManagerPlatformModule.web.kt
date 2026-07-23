package com.github.enteraname74.soulsearching.features.filemanager.di

import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManagerWebImpl
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManagerWebImpl
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val fileManagerPlatformModule: Module = module {
    singleOf(::CoverFileManagerWebImpl) bind CoverFileManager::class
    singleOf(::CachedCoverManagerWebImpl) bind CachedCoverManager::class
    singleOf(::MusicMetadataHelper)
}
