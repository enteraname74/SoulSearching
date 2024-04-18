import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.soulsearching.SoulSearchingDesktop

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            SoulSearchingDesktop()
        }
    }
}
