package com.github.enteraname74.localdb

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single {
        RoomPlatformBuilder(
            context = androidApplication()
        )
    }
}
