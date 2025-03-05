package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.localdb.localAndroidModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val localModule: Module = module {
    includes(localAndroidModule)
}