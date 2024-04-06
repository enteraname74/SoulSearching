package com.github.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localdesktop.AppDatabase
import com.github.enteraname74.localdesktop.localDesktopModule
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.viewmodel.MainActivityViewModelDesktopImpl
import org.koin.compose.KoinApplication

@Composable
fun SoulSearchingDesktop() {
    AppDatabase.connectToDatabase()
    KoinApplication(
        application = {
            modules(domainModule, localDesktopModule, appModule)
        }
    ) {
        val colorThemeManager: ColorThemeManager = injectElement()
        SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

        val mainActivityViewModel = injectElement<MainActivityViewModelDesktopImpl>()
        val playbackManager = injectElement<PlaybackManagerDesktopImpl>()

        with(mainActivityViewModel.handler) {
            isReadPermissionGranted = true
            isPostNotificationGranted = true
        }

        SoulSearchingApplication(
            playbackManager = playbackManager
        )
    }

}