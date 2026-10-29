package com.github.enteraname74.soulsearching

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.enteraname74.soulsearching.app.generated.resources.Res
import com.github.enteraname74.soulsearching.app.generated.resources.app_icon
import com.github.enteraname74.soulsearching.coreui.keyboard.GlobalKeyboardShortcutState
import com.github.enteraname74.soulsearching.coreui.keyboard.LocalGlobalKeyboardShortcutState
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.ext.toKeyboardAction
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import java.awt.Frame
import java.awt.Toolkit
import kotlinx.coroutines.runBlocking

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
        val keyboardShortcutState = remember { GlobalKeyboardShortcutState() }
        val windowState = rememberSoulSearchingWindow()
        Window(
            state = windowState,
            onCloseRequest = ::exitApplication,
            title = CoreRes.string.app_name.toTextSpec().string,
            icon = painterResource(Res.drawable.app_icon),
            onPreviewKeyEvent = {
                if (keyboardShortcutState.isBlocked) {
                    false
                } else {
                    val action = it.toKeyboardAction()
                    if (action != null) {
                        playbackManager.handleKeyboardAction(action = action)
                    }
                    action != null
                }
            }
        ) {
            ListenToWindowSizeChange(windowState = windowState)
            CompositionLocalProvider(
                LocalGlobalKeyboardShortcutState provides keyboardShortcutState,
            ) {
                SoulSearchingApplication()
            }
        }
    }
}

@Composable
private fun rememberSoulSearchingWindow(
    settingsRepository: SettingsRepository = injectElement(),
): WindowState {
    val settings = remember { runBlocking { settingsRepository.getSettings().desktop } }
    return rememberWindowState(
        size = DpSize(
            width = settings.windowWidth.dp,
            height = settings.windowHeight.dp
                + Toolkit.getDefaultToolkit().getScreenInsets(Frame().graphicsConfiguration).top.dp,
        ),
        placement = if (settings.isFullscreen) {
            WindowPlacement.Fullscreen
        } else {
            WindowPlacement.Floating
        },
    )
}

@Composable
private fun ListenToWindowSizeChange(
    settingsRepository: SettingsRepository = injectElement(),
    windowState: WindowState,
) {
    val windowHeight: Dp = windowState.size.height
    val windowWidth = windowState.size.width

    LaunchedEffect(windowHeight) {
        settingsRepository.setWindowHeight(windowHeight.value)
    }

    LaunchedEffect(windowWidth) {
        settingsRepository.setWindowWidth(windowWidth.value)
    }

    LaunchedEffect(windowState.placement) {
        settingsRepository.setFullscreen(windowState.placement == WindowPlacement.Fullscreen)
    }
}
