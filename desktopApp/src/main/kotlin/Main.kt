import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.SoulSearchingDesktop
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.desktopapp.desktopapp.generated.resources.Res
import com.github.enteraname74.soulsearching.desktopapp.desktopapp.generated.resources.app_icon
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication

fun main() = application {

    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        val settings: SoulSearchingSettings = injectElement()
        val playbackManager: PlaybackManager = injectElement()
        val windowState = rememberWindowState(
            size = DpSize(
                width = settings.get(SoulSearchingSettingsKeys.Desktop.WINDOW_WIDTH).toDp(),
                height = settings.get(SoulSearchingSettingsKeys.Desktop.WINDOW_HEIGHT).toDp(),
            ),
            placement = if (settings.get(SoulSearchingSettingsKeys.Desktop.IS_FULLSCREEN)) {
                WindowPlacement.Fullscreen
            } else {
                WindowPlacement.Floating
            },
        )

        LaunchedEffect(windowState.size) {
            settings.set(
                key = SoulSearchingSettingsKeys.Desktop.WINDOW_WIDTH.key,
                value = windowState.size.width.value.toInt(),
            )
            settings.set(
                key = SoulSearchingSettingsKeys.Desktop.WINDOW_HEIGHT.key,
                value = windowState.size.height.value.toInt(),
            )
        }

        LaunchedEffect(windowState.placement) {
            settings.set(
                key = SoulSearchingSettingsKeys.Desktop.IS_FULLSCREEN.key,
                value = windowState.placement == WindowPlacement.Fullscreen,
            )
        }

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
                SoulSearchingDesktop()
            }
        }
    }
}