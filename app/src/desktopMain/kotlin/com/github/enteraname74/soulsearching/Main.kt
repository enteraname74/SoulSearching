package com.github.enteraname74.soulsearching

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.app_icon
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.shareddi.LocalDatabaseInitializer
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import java.awt.Frame
import java.awt.Toolkit

fun main() = application {
    FileKit.init(appId = "io.github.enteraname74.soulsearching")
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        val settings: SoulSearchingSettings = injectElement()
        val playbackManager: PlaybackManager = injectElement()
        val windowState = rememberWindowState(
            size = DpSize(
                width = settings.get(SoulSearchingSettingsKeys.Desktop.WINDOW_WIDTH).dp,
                height = settings.get(SoulSearchingSettingsKeys.Desktop.WINDOW_HEIGHT).dp
                        + Toolkit.getDefaultToolkit().getScreenInsets(Frame().graphicsConfiguration).top.dp,
            ),
            placement = if (settings.get(SoulSearchingSettingsKeys.Desktop.IS_FULLSCREEN)) {
                WindowPlacement.Fullscreen
            } else {
                WindowPlacement.Floating
            },
        )
        Window(
            state = windowState,
            onCloseRequest = ::exitApplication,
            title = strings.appName,
            icon = painterResource(Res.drawable.app_icon),
            onPreviewKeyEvent = {
                playbackManager.handleKeyEvent(event = it)
                false
            }
        ) {
            MaterialTheme {
                val windowHeight: Dp = windowState.size.height
                val windowWidth = windowState.size.width

                LaunchedEffect(windowHeight) {
                    settings.set(
                        key = SoulSearchingSettingsKeys.Desktop.WINDOW_HEIGHT.key,
                        value = windowHeight.value,
                    )
                }

                LaunchedEffect(windowWidth) {
                    settings.set(
                        key = SoulSearchingSettingsKeys.Desktop.WINDOW_WIDTH.key,
                        value = windowWidth.value,
                    )
                }

                LaunchedEffect(windowState.placement) {
                    settings.set(
                        key = SoulSearchingSettingsKeys.Desktop.IS_FULLSCREEN.key,
                        value = windowState.placement == WindowPlacement.Fullscreen,
                    )
                }

                SoulSearchingDesktop()
            }
        }
    }
}

@Composable
private fun SoulSearchingDesktop() {
    LocalDatabaseInitializer.init()
    val applicationViewModel = injectElement<ApplicationViewModel>()

    with(applicationViewModel) {
        isReadPermissionGranted = true
        isPostNotificationGranted = true
    }

    SoulSearchingApplication()
}