package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.feature.settings.developers.SettingsDevelopersScreen

/**
 * Represent the view of the settings about screen.
 */
class SettingsAboutScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsAboutScreenView(
            finishAction = {
                navigator.pop()
            },
            navigateToDevelopers = {
                navigator.push(
                    SettingsDevelopersScreen()
                )
            }
        )
    }
}

@Composable
expect fun SettingsAboutScreenView(
    finishAction: () -> Unit,
    navigateToDevelopers: () -> Unit,
)
