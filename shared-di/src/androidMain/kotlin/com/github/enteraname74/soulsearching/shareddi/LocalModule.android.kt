package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.localdb.localAndroidModule
import org.koin.core.module.Module
import org.koin.dsl.module
import com.github.enteraname74.domain.util.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual val localModule: Module = module {
    includes(localAndroidModule)
    singleOf(::CoverFileManagerAndroidImpl) bind CoverFileManager::class
}