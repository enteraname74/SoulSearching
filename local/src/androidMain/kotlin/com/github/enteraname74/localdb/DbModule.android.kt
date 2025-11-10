package com.github.enteraname74.localdb

import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication

internal actual val platformModule: Module = module {
    single {
        RoomPlatformBuilder(
            context = androidApplication()
        )
    }
}