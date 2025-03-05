package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.soulsearching.localdesktop.localDesktopModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val localModule: Module = module {
    includes(localDesktopModule)
}