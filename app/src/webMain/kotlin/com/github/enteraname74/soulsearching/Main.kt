package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.toKeyboardAction
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(
        viewportContainer = document.getElementById("root") as HTMLElement,
    ) {
        KoinApplication(
            configuration = koinConfiguration(
                declaration = {
                    modules(appModule)
                }
            ),
        ) {
            val applicationViewModel = injectElement<ApplicationViewModel>()

            with(applicationViewModel) {
                isReadPermissionGranted = SoulSearchingContext.checkIfReadPermissionGranted()
                isPostNotificationGranted = SoulSearchingContext.checkIfPostNotificationGranted()
            }
            GlobalPlaybackKeyboardListener()
            SoulSearchingApplication()
        }
    }
}

@Composable
fun GlobalPlaybackKeyboardListener(
    playbackManager: PlaybackManager = injectElement(),
) {
    DisposableEffect(playbackManager) {

        val listener: (Event) -> Unit = listener@{ event ->
            val keyboardEvent = event as? KeyboardEvent
                ?: return@listener

            // Only on key release (same behavior as desktop)
            if (keyboardEvent.type != "keyup") {
                return@listener
            }

            // Don't trigger shortcuts while typing
            val target = keyboardEvent.target

            if (
                target is HTMLInputElement ||
                target is HTMLTextAreaElement ||
                target is HTMLSelectElement
            ) {
                return@listener
            }

            val keyboardAction = keyboardEvent.toKeyboardAction()
            if (keyboardAction != null) {
                keyboardEvent.preventDefault()
                playbackManager.handleKeyboardAction(keyboardAction)
            }
        }

        window.addEventListener(
            type = "keyup",
            callback = listener,
        )

        onDispose {
            window.removeEventListener(
                type = "keyup",
                callback = listener,
            )
        }
    }
}