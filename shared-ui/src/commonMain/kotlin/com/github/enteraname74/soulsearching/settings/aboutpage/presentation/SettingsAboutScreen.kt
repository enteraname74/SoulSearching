package com.github.soulsearching.settings.aboutpage.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.settings.developers.presentation.SettingsDevelopersScreen

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
