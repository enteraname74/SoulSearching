package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.enteraname74.soulsearching.shareddi.LocalDatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication

@Composable
fun SoulSearchingDesktop() {
    LocalDatabaseInitializer.init()
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        val useCase: GetAllMusicUseCase = injectElement()
//        CoroutineScope(Dispatchers.IO).launch {
//            println("Will listen on use case")
//            println("Got musics? ${useCase().first().size}")
//            useCase().collect { musics ->
//                println("Got music! $musics")
//            }
//        }
        val colorThemeManager: ColorThemeManager = injectElement()
        SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

        val mainActivityViewModel = injectElement<MainActivityViewModel>()
        val playbackManager = injectElement<PlaybackManagerDesktopImpl>()

        with(mainActivityViewModel) {
            isReadPermissionGranted = true
            isPostNotificationGranted = true
        }

        SoulSearchingApplication(
            playbackManager = playbackManager
        )
    }

}