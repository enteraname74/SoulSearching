package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.util.AppEnvironment
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import com.github.enteraname74.soulsearching.util.FileOperation
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val nonAndroidModule: Module = module {
    single<SoulSearchingSettings> {
        val suffix = if (AppEnvironment.IS_IN_DEVELOPMENT) {
            "/dev"
        } else {
            ""
        }
        SoulSearchingSettingsImpl(
            settings = PreferencesSettings(
                delegate = Preferences.userRoot().node("com/github/enteraname74/soulsearching$suffix")
            )
        )
    }
    singleOf(::MusicLinkHandler)
    factory {
        FileOperation()
    }
}