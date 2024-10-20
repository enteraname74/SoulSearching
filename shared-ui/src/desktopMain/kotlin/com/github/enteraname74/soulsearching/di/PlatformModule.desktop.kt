package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpDesktopManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingDesktopPlayerImpl
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    singleOf(::SoulSearchingDesktopPlayerImpl)
    singleOf(::FeedbackPopUpDesktopManager) bind FeedbackPopUpManager::class
    single<SoulSearchingSettings> {
        SoulSearchingSettingsImpl(
            settings = PreferencesSettings(
                delegate = Preferences.userRoot().node("com/github/enteraname74/soulsearching")
            )
        )
    }
}