package com.github.enteraname74.localdb

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<Settings> { StorageSettings() }
    single { RoomPlatformBuilder() }
}
