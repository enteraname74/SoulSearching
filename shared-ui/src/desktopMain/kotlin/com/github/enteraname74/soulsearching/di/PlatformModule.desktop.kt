package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.util.AppEnvironment
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpDesktopManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingDesktopPlayerImpl
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.SettingsCloudUploadLauncher
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.SettingsCloudUploadLauncherDesktopImpl
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
    singleOf(::SettingsCloudUploadLauncherDesktopImpl) bind SettingsCloudUploadLauncher::class
}