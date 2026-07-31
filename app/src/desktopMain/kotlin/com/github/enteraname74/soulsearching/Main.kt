package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.app_icon
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.toKeyboardAction
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import java.awt.Frame
import java.awt.Toolkit

fun main(): Unit = application {
    FileKit.init(appId = "io.github.enteraname74.soulsearching")
    KoinApplication(
        configuration = koinConfiguration(
            declaration = {
                modules(appModule)
            }
        ),
    ) {
        val playbackManager: PlaybackManager = injectElement()
        val windowState = rememberSoulSearchingWindow()
        Window(
            state = windowState,
            onCloseRequest = ::exitApplication,
            title = strings.appName,
            icon = painterResource(Res.drawable.app_icon),
            onPreviewKeyEvent = {
                val action = it.toKeyboardAction()
                if (action != null) {
                    playbackManager.handleKeyboardAction(action = action)
                }
                action != null
            }
        ) {
            ListenToWindowSizeChange(windowState = windowState)
            val applicationViewModel = injectElement<ApplicationViewModel>()

            with(applicationViewModel) {
                isReadPermissionGranted = SoulSearchingContext.checkIfReadPermissionGranted()
                isPostNotificationGranted = SoulSearchingContext.checkIfPostNotificationGranted()
            }

            SoulSearchingApplication()
        }
    }
}

@Composable
private fun rememberSoulSearchingWindow(
    settings: SoulSearchingSettings = injectElement(),
): WindowState {
    return rememberWindowState(
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
}

@Composable
private fun ListenToWindowSizeChange(
    settings: SoulSearchingSettings = injectElement(),
    windowState: WindowState,
) {
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
}
