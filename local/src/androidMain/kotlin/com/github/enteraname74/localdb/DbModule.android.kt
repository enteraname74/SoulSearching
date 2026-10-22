package com.github.enteraname74.localdb

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<Settings> {
        SharedPreferencesSettings(get<Context>().getSharedPreferences("SOUL_SEARCHING_SHARED_PREF", Context.MODE_PRIVATE))
    }
    single {
        RoomPlatformBuilder(
            context = get<Context>()
        )
    }
}
