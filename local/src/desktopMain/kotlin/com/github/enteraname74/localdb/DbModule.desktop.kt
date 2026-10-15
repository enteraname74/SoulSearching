package com.github.enteraname74.localdb

import com.github.enteraname74.soulsearching.domain.util.AppDirectories
import com.github.enteraname74.soulsearching.domain.util.AppEnvironment
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.prefs.Preferences

internal actual val platformModule: Module = module {
    single<Settings> {
        System.setProperty("java.util.prefs.userRoot", AppDirectories.config.toString())
        val suffix = if (AppEnvironment.IS_IN_DEVELOPMENT) "/dev" else ""
        PreferencesSettings(Preferences.userRoot().node("com/github/enteraname74/soulsearching$suffix"))
    }
    single { RoomPlatformBuilder() }
}
