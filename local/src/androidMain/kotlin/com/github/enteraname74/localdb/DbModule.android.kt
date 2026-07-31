package com.github.enteraname74.localdb

import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single {
        RoomPlatformBuilder(
            context = get<Context>()
        )
    }
}
