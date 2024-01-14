import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localdb.localDbModule
import com.github.soulsearching.Application
import com.github.soulsearching.appModule
import com.github.soulsearching.strings
import org.koin.compose.KoinApplication

fun main() = application {
    KoinApplication(
        application = {
            modules(appModule, domainModule, localDbModule)
        }
    ) {
        Window(
            title = strings.appName,
            onCloseRequest = ::exitApplication,
            state = WindowState(
                width = 1000.dp,
                height = 700.dp
            )
        ) {
            Application()
        }
    }
}