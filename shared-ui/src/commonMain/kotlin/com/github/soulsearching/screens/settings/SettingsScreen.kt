package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme

/**
 * Represent the view of the settings screen.
 */
class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsScreenView(
            finishAction = {
                navigator.pop()
            },
            navigateToAbout = {
                navigator.push(
                    SettingsAboutScreen()
                )
            },
            navigateToColorTheme = {
                navigator.push(
                    SettingsColorThemeScreen()
                )
            },
            navigateToManageMusics = {
                navigator.push(
                    SettingsManageMusicsScreen()
                )
            },
            navigateToPersonalisation = {
                navigator.push(
                    SettingsPersonalisationScreen()
                )
            }
        )
    }

    @Composable
    private fun SettingsScreenView(
        finishAction: () -> Unit,
        navigateToManageMusics: () -> Unit,
        navigateToColorTheme: () -> Unit,
        navigateToPersonalisation: () -> Unit,
        navigateToAbout: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoulSearchingColorTheme.colorScheme.primary)
        ) {
            AppHeaderBar(
                title = strings.settings,
                leftAction = finishAction
            )
            LazyColumn {
                item {
                    SettingsElement(
                        title = strings.manageMusicsTitle,
                        text = strings.manageMusicsText,
                        icon = Icons.Rounded.MusicNote,
                        clickAction = navigateToManageMusics
                    )
                }
                item {
                    SettingsElement(
                        title = strings.colorThemeTitle,
                        text = strings.colorThemeText,
                        icon = Icons.Rounded.Palette,
                        clickAction = navigateToColorTheme
                    )
                }
                item {
                    SettingsElement(
                        title = strings.personalizationTitle,
                        text = strings.personalizationText,
                        icon = Icons.Rounded.Edit,
                        clickAction = navigateToPersonalisation
                    )
                }
                item {
                    SettingsElement(
                        title = strings.aboutTitle,
                        text = strings.aboutText,
                        icon = Icons.Rounded.Info,
                        clickAction = navigateToAbout
                    )
                }
                item {
                    PlayerSpacer()
                }
            }
        }
    }
}