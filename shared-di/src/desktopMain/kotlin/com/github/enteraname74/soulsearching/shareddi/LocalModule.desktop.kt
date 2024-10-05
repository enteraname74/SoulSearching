package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.domain.util.*
import com.github.enteraname74.soulsearching.localdesktop.localDesktopModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val localModule: Module = module {
    includes(localDesktopModule)
    singleOf(::CoverFileManagerDesktopImpl) bind CoverFileManager::class
}