package com.github.enteraname74.soulsearching.features.filemanager.di

import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManagerAndroidImpl
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManagerAndroidImpl
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManager
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManagerAndroidImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val fileManagerPlatformModule: Module = module {
    singleOf(::CoverFileManagerAndroidImpl) bind CoverFileManager::class
    singleOf(::CachedCoverManagerAndroidImpl) bind CachedCoverManager::class
    singleOf(::CloudCacheManagerAndroidImpl) bind CloudCacheManager::class
}