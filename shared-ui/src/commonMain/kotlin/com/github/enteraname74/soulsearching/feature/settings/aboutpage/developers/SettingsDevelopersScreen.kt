package com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement

/**
 * Represent the view of the developers screen.
 */
class SettingsDevelopersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsDevelopersScreenView(
            navigateBack = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun SettingsDevelopersScreenView(
        navigateBack: () -> Unit
    ) {
        val developers = listOf(
            Developer(
                name = "Noah Penin",
                function = strings.leadDeveloper,
                link = "https://github.com/enteraname74"
            )
        )

        val uriHandler = LocalUriHandler.current

        SettingPage(
            navigateBack = navigateBack,
            title = strings.developersTitle,
        ) {
            items(developers) { developer ->
                SettingsElement(
                    title = developer.name,
                    subTitle = developer.function,
                    onClick = {
                        uriHandler.openUri(
                            uri = developer.link,
                        )
                    }
                )
            }
        }
    }
}