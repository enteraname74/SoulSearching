package com.github.enteraname74.soulsearching

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.koinConfiguration
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.getElementById("root") as HTMLElement) {
        KoinApplication(
            configuration = koinConfiguration(
                declaration = { modules(appModule) }),
            content = {
                val applicationViewModel = injectElement<ApplicationViewModel>()

                with(applicationViewModel) {
                    isReadPermissionGranted = SoulSearchingContext.checkIfReadPermissionGranted()
                    isPostNotificationGranted = SoulSearchingContext.checkIfPostNotificationGranted()
                }
                SoulSearchingApplication()
            })
    }
}