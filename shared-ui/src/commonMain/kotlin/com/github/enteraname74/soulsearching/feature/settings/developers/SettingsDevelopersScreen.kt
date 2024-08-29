package com.github.enteraname74.soulsearching.feature.settings.developers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement

/**
 * Represent the view of the developers screen.
 */
class SettingsDevelopersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsDevelopersScreenView(
            finishAction = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun SettingsDevelopersScreenView(
        finishAction: () -> Unit
    ) {
        val developers = listOf(
            Developer(
                name = "Noah Penin",
                function = strings.leadDeveloper,
                link = "https://github.com/enteraname74"
            )
        )

        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoulSearchingColorTheme.colorScheme.primary)
        ) {
            SoulTopBar(
                title = strings.developersTitle,
                leftAction = TopBarNavigationAction(
                    onClick = finishAction,
                )
            )
            LazyColumn {
                items(developers) { developer ->
                    SettingsElement(
                        title = developer.name,
                        text = developer.function,
                        onClick = {
                            uriHandler.openUri(
                                uri = developer.link,
                            )
                        }
                    )
                }
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}