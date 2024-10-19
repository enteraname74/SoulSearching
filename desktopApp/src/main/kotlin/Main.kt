import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.enteraname74.soulsearching.SoulSearchingDesktop
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
        val playbackManager: PlaybackManager = injectElement()

        Window(
            onCloseRequest = ::exitApplication,
            title = "Soul Searching",
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