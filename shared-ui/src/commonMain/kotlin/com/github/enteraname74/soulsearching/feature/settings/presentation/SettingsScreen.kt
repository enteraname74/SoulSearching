package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.SettingsAboutScreen
import com.github.enteraname74.soulsearching.feature.settings.colortheme.SettingsColorThemeScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsScreen
import com.github.enteraname74.soulsearching.feature.settings.personalisation.SettingsPersonalisationScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement

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
                navigator.safePush(
                    SettingsAboutScreen()
                )
            },
            navigateToColorTheme = {
                navigator.safePush(
                    SettingsColorThemeScreen()
                )
            },
            navigateToManageMusics = {
                navigator.safePush(
                    SettingsManageMusicsScreen()
                )
            },
            navigateToPersonalisation = {
                navigator.safePush(
                    SettingsPersonalisationScreen()
                )
            }
        )
    }
}

@Composable
fun SettingsScreenView(
    finishAction: () -> Unit,
    navigateToManageMusics: () -> Unit,
    navigateToColorTheme: () -> Unit,
    navigateToPersonalisation: () -> Unit,
    navigateToAbout: () -> Unit
) {
    SettingPage(
        navigateBack = finishAction,
        title = strings.settings,
    ) {
        item {
            SettingsElement(
                title = strings.manageMusicsTitle,
                subTitle = strings.manageMusicsText,
                icon = Icons.Rounded.MusicNote,
                onClick = navigateToManageMusics
            )
        }
        item {
            SettingsElement(
                title = strings.colorThemeTitle,
                subTitle = strings.colorThemeText,
                icon = Icons.Rounded.Palette,
                onClick = navigateToColorTheme
            )
        }
        item {
            SettingsElement(
                title = strings.personalizationTitle,
                subTitle = strings.personalizationText,
                icon = Icons.Rounded.Edit,
                onClick = navigateToPersonalisation
            )
        }
        item {
            SettingsElement(
                title = strings.aboutTitle,
                subTitle = strings.aboutText,
                icon = Icons.Rounded.Info,
                onClick = navigateToAbout
            )
        }
    }
}