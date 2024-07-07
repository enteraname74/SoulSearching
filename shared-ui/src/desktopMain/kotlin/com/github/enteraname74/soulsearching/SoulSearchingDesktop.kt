package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localdesktop.AppDatabase
import com.github.enteraname74.localdesktop.localDesktopModule
import com.github.enteraname74.soulsearching.SoulSearchingApplication
import com.github.enteraname74.soulsearching.appModule
import com.github.enteraname74.soulsearching.commonModule
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.viewmodel.MainActivityViewModelDesktopImpl
import org.koin.compose.KoinApplication

@Composable
fun SoulSearchingDesktop() {
    AppDatabase.connectToDatabase()
    KoinApplication(
        application = {
            modules(domainModule, localDesktopModule, appModule, commonModule)
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